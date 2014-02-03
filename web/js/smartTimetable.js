var closeButton = '<button type="button" class="close" data-dismiss="alert">×</button>';

// set to true when the session info has loaded
var isSessionFinishedLoading = false;

// used to keep track of/cancel timeouts set to clear the error message div
var errorAlertTimeout;

// keeps track of the last preference the user clicked
var lastBreakElementSelected;
var lastStartTimeElementSelected;
var lastEndTimeElementSelected;

/**
 * Add an error message to the page
 * @param String message
 */
function addErrorMessage(message) {
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


// parse xml add course response
function processAddCourseResponse(response) {
    if (response == undefined) {
        console.log("undefined response returned");
        return;
    }
    var course = response.getElementsByTagName("course")[0];
    if (course != undefined) {
        // success case
        console.log("Course response returned");
        var courseName = course.getElementsByTagName("courseName")[0].childNodes[0].nodeValue;
        var courseTerm = course.getElementsByTagName("term")[0].childNodes[0].nodeValue;
        var url = course.getElementsByTagName("url")[0].childNodes[0].nodeValue;
        addCoursetoList(courseName, courseTerm, url);
    }
    else {
        // error case
        addErrorMsg(response);
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

function addCoursetoList(courseName, courseTerm, url) {
    var tableBody = $('#courses-table-body');
    var index = 1;

    if (courseTerm == "null") {
        courseTerm = '-';
    }

    // if tableBody exists
    if (tableBody.length) {
        index = tableBody.children().length + 1;
        console.log("Adding to existing course table");
    }
    else { // tableBody does not exist. make one
        console.log("Making course table");
        
        var div = $('#courses-table');
        div.html('  <div class="btn-toolbar">\
                        <button class="btn btn-info">Save this List</button>\
                        <button class="btn btn-info">Import Saved List</button>\
                    </div><div class="well"><table class="table">\
                            <thead><tr>\
                                    <th>#</th>\
                                    <th>Course Name</th>\
                                    <th>Term</th>\
                                    <th>Username</th>\
                                    <th></th>\
                                    <th></th>\
                                </tr></thead>\
                            <tbody id="courses-table-body">\
                            </tbody></table></div>');
        tableBody = $('#courses-table-body');
    }
    // add the new course's information with a new row in the table
    var courseClass = courseName.replace(" ", "");
    tableBody.append('<tr><td>' + index + '</td>\
                          <td><a href="' + url + '" target="_blank" class="link">' + courseName + '</a></td>\
                          <td>' + courseTerm + '</td>\
                          <td>[username]</td>\
                          <td><div class="course-modal ' + courseClass + '"></div></td>\
                          <td><a href="removeCourse?course='+ courseName +'" role="button" data-toggle="modal">\
                                  <i class="icon-remove" title="Remove Course"></i>\
                              </a></td></tr>');
    
    courseClass = "." + courseClass;
    setTimeout(function() {
        // TODO hook this up to an ajax call
        $(courseClass).css("display", "none");
    }, 5000);
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
    sessionDropdown.html('Session: ' + activeSession + '<strong class="caret"></strong>');
    
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
        error: function(data) {
            console.log("Change session ajax Error");
            console.log(data);
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
                    processAddCourseResponse(data);
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
        $('#preferencesPanel').toggle();
    });
    
    // toggle the visibility of the tree's children on click events
    $('label.tree-toggler').click(function() {
        $(this).parent().children('ul.tree').toggle(300);
    });
    
    // initially the preference panel and hide all its trees
    $('#preferencesPanel').hide();
    $('label.tree-toggler').parent().children('ul.tree').toggle(300);
    
    
    $('.nav-list .tree a').click(function(event) {
        event.preventDefault();
        var element = $(this);
        var parent = element.parent().parent();
        var text = element.text()
        var queryString;
        var elementSelected;
        
        if (parent.hasClass("dayoff-term1")) {
            var dayInt = dayToInt(text);
            queryString = "dayoffTerm1=" + dayInt;
            elementSelected = "dayoff";
            
        } else if (parent.hasClass("dayoff-term2")) {
            var dayInt = dayToInt(text);
            queryString = "dayoffTerm2=" + dayInt;
            elementSelected = "dayoff";
            
        } else if (parent.hasClass("earliestClass")) {
            var time = text.split(":")[0];
            queryString = "startTime=" + time;
            elementSelected = "earliestClass";
            
        } else if (parent.hasClass("latestClass")) {
            var time = text.split(":")[0];
            queryString = "endTime=" + time;
            elementSelected = "latestClass";
            
        } else {
            var hour = text.split(" Hour")[0];
            var hourInt = parseInt(hour);
            if (hourInt) {
                queryString = "breakLength=" + hourInt;
            } else if (text.indexOf("little") != -1) {
                queryString = "breakLength=" + 0;
            } else {
                queryString = "breakLength=" + 6;
            }
        }
        
        $.ajax({
            url: "/SmartTimetable/preferences?" + queryString,
            dataType: "xml",
            success: function(data) {
                console.log("preferences ajax Success");
                
                switch(elementSelected) {
                    case "dayoff":
                        if (element.text().indexOf("--") === -1) {
                            element.css("background-color", '#0E3A63');
                            element.text("-- " + element.text());
                        } else {
                            element.css("background-color", '#f5f5f5');
                            element.text(element.text().split("-- ")[1]);
                        }
                        return;
                    case "earliestClass":
                        if (lastStartTimeElementSelected) {
                            var el = lastStartTimeElementSelected;
                            el.css("background-color", '#f5f5f5');
                            el.text(el.text().split("-- ")[1]);
                        }
                        lastStartTimeElementSelected = element;
                        break;
                    case "latestClass":
                        if (lastEndTimeElementSelected) {
                            var el = lastEndTimeElementSelected;
                            el.css("background-color", '#f5f5f5');
                            el.text(el.text().split("-- ")[1]);
                        }
                        lastEndTimeElementSelected = element;
                        break;
                    default:
                        if (lastBreakElementSelected) {
                            var el = lastBreakElementSelected;
                            el.css("background-color", '#f5f5f5');
                            el.text(el.text().split("-- ")[1]);
                        }
                        lastBreakElementSelected = element;
                        break;
                }
                element.css("background-color", '#0E3A63');
                element.text("-- " + element.text());
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
