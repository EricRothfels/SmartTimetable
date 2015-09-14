var closeButton = '<button type="button" class="close" data-dismiss="alert">×</button>';

// set to true when the session info has loaded
var isSessionFinishedLoading = false;

// used to keep track of/cancel timeouts set to clear the error message div
var errorAlertTimeout;


/**
 * Add an error message to the page
 * @param String message
 */
function addErrorMessage(message) {
    if (message.indexOf(':') > -1) {
        console.log(message);
        // remove error type when displaying to users
        message = message.substring(message.indexOf(':') + 1);
    }
    var div = $('#addCourseError');
    div.html(  '<div class="alert alert-block alert-error fade in">' +
        closeButton +
       '<h4 class="alert-heading"></h4>\
        <p><strong>' + message + '</strong></p>\
        </div>');
    div.show();
    
    // clear previous timeouts
    clearTimeout(errorAlertTimeout);
    
    // make error disappear after 5s
    errorAlertTimeout = setTimeout(function() {
        clearErrorMsg();
    }, 5000);
}

function addErrorMsg(response) {
    var error = response.getElementsByTagName("error")[0];
    if (error != undefined) {
        error = error.childNodes[0].nodeValue;
        addErrorMessage(error);
    } else {
        console.log("Bad Error response returned");
    }
}

// clear error messages
function clearErrorMsg() {
    $('#addCourseError').fadeOut(600);
}

function clearCourseListAndTimetables() {
    $('#courses-table-body').html('');
    $('#timetable-container').html('');
}

function sendPopulateCourseAjax(courseName) {
    var courseClass = courseName.replace(" ", "");
    $.ajax({
        url: "/SmartTimetable/populateCourse?courseName=" + courseClass,
        dataType: "xml",
        success: function() {
            console.log("populateCourse " + courseClass + " ajax Success");
            // hide the course loading animation
            $('.' + courseClass).css("display", "none");
        },
        error: function(data) {
            console.log("populateCourse " + courseClass + " ajax Error");
            addErrorMsg(data);
        }
    });
}


// parse xml add course response
function processAddCourseResponse(response) {
    if (response == undefined) {
        console.log("undefined response returned");
        return false;
    }
    var course = response.getElementsByTagName("course")[0];
    if (course != undefined) {
        // success case
        console.log("Course response returned");
        var courseName = course.getElementsByTagName("courseName")[0].childNodes[0].nodeValue;
        var url = course.getElementsByTagName("url")[0].childNodes[0].nodeValue;
        
        // add the course to the course list table
        return addCoursetoList(courseName, url);
    }
    else {
        // error case
        addErrorMsg(response);
        return false;
    }
}

/**
 * Called after a maketimetables ajax request returns from the server
 * @param xml response string: the xml response from the server
 * containing one of timetable, conflict, or error data
 */
function processMakeTimetablesResponse(response) {
    var timetables = response.getElementsByTagName("timetable");
    if (timetables != undefined && timetables.length > 0) {
        console.log("timetables response returned");
        console.log("timetables.length: " + timetables.length);
        
        // display timetables graphically in ui
        buildTimetables(timetables);
        return;
    }
    var conflicts = response.getElementsByTagName("conflict");
    if (conflicts != undefined && conflicts.length > 0) {
        console.log("conflicts response returned");
        
        // display conflicts graphically in ui
        buildConflicts(conflicts);
        addErrorMessage("No Timetables could be made due to the " +
            "conflicting courses shown below");
        return;
    }
    // error msg returned
    addErrorMsg(response);
}

function addCoursetoList(courseName, url) {
    var tableBody = $('#courses-table-body');
    var index = 1;

    // if tableBody exists
    if (tableBody.length) {
        index = tableBody.children().length + 1;
    }
    else { // tableBody does not exist. make one
        var div = $('#courses-table');
        div.html('  <div class="btn-toolbar">\
                        <button class="btn btn-info">Save this List</button>\
                        <button class="btn btn-info">Import Saved List</button>\
                    </div><div class="well">\
                    <a style="float:right;" href="removeCourse?course=REMOVE_ALL" role="button" data-toggle="modal">\
                        <i class="icon-remove" title="Remove All Courses"></i>\
                    </a>\
                    <table class="table">\
                            <thead><tr>\
                                    <th>#</th>\
                                    <th></th>\
                                    <th>Course Name</th>\
                                    <th></th>\
                                </tr></thead>\
                            <tbody id="courses-table-body">\
                            </tbody></table></div>');
        tableBody = $('#courses-table-body');
    }
    // add the new course's information with a new row in the table
    var courseClass = courseName.replace(" ", "");
    tableBody.append('<tr><td>' + index + '</td>\\n\
                          <td><div class="course-modal ' + courseClass + '"></div></td>\
                          <td><a href="' + url + '" target="_blank" class="link">' + courseName + '</a></td>\
                          <td><a href="removeCourse?course='+ courseName +'" role="button" data-toggle="modal">\
                                  <i class="icon-remove" title="Remove '+ courseName +'"></i>\
                              </a></td></tr>');
    return courseName;
}

function addSessionDropdownData(data) {
    var sessionList = data.getElementsByTagName("session");
    
    var sessionString = '';
    for (var i=0; i < sessionList.length; i++) {
        sessionString += '<li><a href="#" class="sessionOption">' +
                sessionList[i].childNodes[0].nodeValue + '</a></li>';
    }
    $("#session-dropdown-menu").html(sessionString);

    var sessionDropdown = $("#session-dropdown");
    
    var activeSession = data.getElementsByTagName("activeSession")[0].childNodes[0].nodeValue;
    sessionDropdown.html(sessionDropdown.html() + ' Session: ' + activeSession + '<strong class="caret"></strong>');
    
    addChangeSessionHandler();
}

/**
 * Populates the Session dropdown menu with sessions from the
 * Ubc course web page
 */
function initSessionDropdown() {
    // block the UI until the ajax call returns
    blockUI();
    
    $.ajax({
                type: "get",
                url: "/SmartTimetable/session?getSession=true",
                dataType: "xml",
                success: function(data) {
                    // Call this function on success
                    console.log("Get Session ajax Success");
                    addSessionDropdownData(data);
                    isSessionFinishedLoading = true;
                },
                error: function(data) {
                    console.log("Get Session ajax Error:");
                    addErrorMsg(data);
                }
        });
}

function validateAddCourseForm() {
    $("#addCourseForm").validate({
        rules: {
            courseInput: {
                required: true,
                regex: "^[a-zA-Z]{2,4}\\s?\\d{3}[a-zA-Z]?$"
            }
        },
        messages: {
            courseInput: {
                required: closeButton + 'This field is required.'
            }
        }
    });

    $.validator.addMethod(
            "regex",
            function(value, element, regexp) {
                var re = new RegExp(regexp);
                return this.optional(element) || re.test(value);
            },
            closeButton +
             '<strong>Please enter a course code</strong><br>\
              eg. Engl 112'
            );
}


function sendAjax(requestData, url, title, dropdown) {
    $.ajax({
        url: "/SmartTimetable/" + url,
        data: requestData,

        success: function(data) {
            // Call this function on success
            console.log("Change session ajax Success");
            dropdown.html(title + requestData + " " + '<strong class="caret"></strong>');
            return data;
        },
        error: function() {
            console.log("Change session ajax Error");
        }
    });
}


function addChangeSessionHandler() {
    var sessionDropdown = $("#session-dropdown");
    
    // bind to the click event
    $(".sessionOption").click(function(event) {
        event.preventDefault();
        var session = $(this).text();
        if (sessionDropdown.text().indexOf(session) === -1) {
            sendAjax(session, "session", "Session: ", sessionDropdown);
            clearCourseListAndTimetables();
        }
    });
}

function initChangeCampusHandler() {
    var campusDropdown = $("#campus-dropdown");
    
    // bind to the click event
    $("#campusOption1, #campusOption2").click(function(event) {
        event.preventDefault();
        var campus = $(this).text();
        if (campusDropdown.text().indexOf(campus) === -1) {
            sendAjax(campus, "campus", "Campus: ", campusDropdown);
            clearCourseListAndTimetables();
        }
    });
}

function initMakeTimetableHandler() {
    // bind to the click event
    $("#makeTimetables").click(function(event) {
        event.preventDefault();
        
        // block the UI until the ajax call returns
        blockUI();
        
        $.ajax({
            url: "/SmartTimetable/makeTimetables",
            dataType: "xml",
            success: function(data) {
                console.log("makeTimetables ajax Success");
                processMakeTimetablesResponse(data);
            },
            error: function(data) {
                console.log("makeTimetables ajax Error");
                console.log(data);
            }
        });
    });
    
}

function initCourseSubmitHandler() {
    // bind to the submit event of our form
    $("#addCourseForm").submit(function(event) {
        //validate form
        if(!$(this).valid()) {
            return false;
        }
        // check if session has loaded
        else if (!isSessionFinishedLoading) {
            return false;
        }
        
        // prevent default posting of form
        event.preventDefault();
        
        // block the UI until the ajax call returns
        blockUI();
        
        var form = $(this);
        // serialize the data in the form
        var serializedData = form.serialize();
        
        // fire off the request to the url
        var request = $.ajax({
                type: "POST",
                url: "/SmartTimetable/addCourse",
                data: serializedData,
                dataType: "xml",
                success: function(data) {
                    //document.body.style.cursor='auto';
                    // Call this function on success
                    console.log("Add course ajax Success");
                    var courseName = processAddCourseResponse(data);
                    
                    if (courseName) {
                        unBlockUI();
                        // start the loading of the course on the server
                        sendPopulateCourseAjax(courseName);
                    }
                    return data;
                },
                error: function(data) {
                    //document.body.style.cursor='auto';
                    console.log("Add course ajax Error:");
                    console.log(data);
                }
        });
    });
}

function initPreferencesHandler() {
    // toggle the preference panel's visibility on click events
    $('.preferencesDropdown').click(function(event) {
        event.stopPropagation();
        event.preventDefault();
        $('#preferencesPanel').toggle(350);
    });
    
    // toggle the visibility of the tree's children on click events
    $('label.tree-toggler').click(function() {
        $(this).parent().children('ul.tree').toggle(300);
    });
    
    // hide pref panel when its close button is clicked
    $('#removePrefPanel').click(function() {
        $('#preferencesPanel').hide(350);
    });
    
    // initially hide the preference panel and hide all its trees
    $('#preferencesPanel').hide();
    $('label.tree-toggler').parent().children('ul.tree').toggle(300);
    
    $('.preferencesInput').click(function(event) {
        var queryString;
        var name = $(this).attr('name');
        
        if (name === "dayoffTerm1" || name === 'dayoffTerm2') {
            queryString = name + "=" + dayToInt($(this).attr('value')) +
                    "&checked=" + this.checked;
        } else if (name === "earliestClass") {
            var time = $(this).attr('value');
            queryString = "startTime=" + time;
        } else if (name === "latestClass") {
            var time = $(this).attr('value');
            queryString = "endTime=" + time;
        } else {
            // breaktime
            var time = $(this).attr('value');
            queryString = "breakLength=" + time;
        }
        $.ajax({
            url: "/SmartTimetable/preferences?" + queryString,
            dataType: "xml",
            success: function(data) {
                console.log("preferences ajax Success");
            },
            error: function(data) {
                console.log("preferences ajax Error");
                console.log(data);
            }
        });
    });
}

function dayToInt(day) {
    switch (day) {
        case "Monday" :
            return 1;
        case "Tuesday" :
            return 2;
        case "Wednesday" :
            return 3;
        case "Thursday" :
            return 4;
        default :
            return 5;
    }
}


// block the UI during ajax calls
function blockUI() {
    $body = $("body");
    $(document).on({
        ajaxStart: function() {
            $body.addClass("loading");
        },
        ajaxStop: function() { 
            $body.removeClass("loading");
            $(this).unbind("ajaxStart");
        }    
    });
}

// disable blocking of the UI during ajax calls
function unBlockUI() {
    $("body").removeClass("loading");
    $(document).unbind("ajaxStart");
}