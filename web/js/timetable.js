var TIMETABLES_PER_PAGE = 6;   // number of timetables to display per 'page'
var xmlTimetables = null;
var pageNumber = null;


// Time class
function Time(startTime, endTime, day, term) {
   this.startTime = startTime;
   this.endTime = endTime;
   this.day = day;
   this.term = term;
}


// activity class
function Activity(name, times, id, type, url) {
   this.name = name;
   this.times = times;
   this.id = id;
   this.type = type;
   this.url = url;
}


function timeStringToFloat(time) {
    var split = time.split(":");
    var timeFloat = parseInt(split[0]);
    if (split[1] !== "00") {
        timeFloat += 0.5;
    }
    return timeFloat;
}

function timeFloatToString(time) {
    var timeString = parseInt(time);
    if (time == timeString) {
        timeString += ":00";
    } else {
        timeString += ":30";
    }
    return timeString;
}

function dayStringToInt(day) {
    var dayInt = 0;
    if (day === "Tue") {
        dayInt = 1;
    } else if (day === "Wed") {
        dayInt = 2;
    } else if (day === "Thu") {
        dayInt = 3;
    } else if (day === "Fri") {
        dayInt = 4;
    }
    return dayInt;
}

function makeTableColumn(startTime, endTime, header, term, day) {
    if (header === "Time") {
        var column = '<table class="table-column"><tr><th><div class="table-div-title-time">' +
                header + '</div></th></tr>';
    } else {
        var column = '<table class="table-column"><tr><th><div class="table-div-title">' +
                header + '</div></th></tr>';
    }
    var tableDiv = '<tr><td><div class="table-div" id="div';
    var endDiv = '"></div></td></tr>';
    var hr = '<tr><td><hr class="table-hr"></td></tr>';
    var hrDashed = '<tr><td><hr class="table-hr-dashed"></td></tr>';
    
    for (var i=startTime; i < endTime; i++) {
        column += hr;
        var row = i - startTime;
        if (header === "Time") {
            column += '<tr><td><div class="table-div-time">' + timeFloatToString(i) + '</div></td></tr>';
            column += hrDashed;
            column += '<tr><td><div class="table-div"></div></td></tr>';
        } else {
            column += tableDiv + row + '-' + day + '-' + term + endDiv;
            column += hrDashed;
            column += tableDiv + row + '-5-' + day + '-' + term + endDiv;
        }
    }
    column += '</table>';
    return column;
}

function makeEmptyTable(startTime, endTime, term) {
    var table = '<div class="timetableDiv-inner">' + makeTableColumn(startTime, endTime, 'Time', term);
    table += makeTableColumn(startTime, endTime, 'Monday', term, 0);
    table += makeTableColumn(startTime, endTime, 'Tuesday', term, 1);
    table += makeTableColumn(startTime, endTime, 'Wednesday', term, 2);
    table += makeTableColumn(startTime, endTime, 'Thursday', term, 3);
    table += makeTableColumn(startTime, endTime, 'Friday', term, 4);
    return table + '</div>';
}

function makeEvent(activity, time, lowTime, tableIds, term) {
    
    if (term === undefined) {
        term = time.term;
        var type = activity.type;
    } else {
        var type = "conflict";
    }
    if (term == "1" || term == "1-2") {
        var tableId = tableIds[0];
    } else if (term == "2" || term == "1-2") {
        var tableId = tableIds[1];
    }
    var startRow = time.startTime - lowTime;
    var endRow = time.endTime - lowTime;
    
    var startInt = parseInt(startRow);
    if (startInt == startRow) {
        var div = $('#' + tableId + ' #div' + startInt + '-' + time.day + '-' + term);
    } else {
        var div = $('#' + tableId + ' #div' + startInt + '-5-' + time.day + '-' + term);
    }
    var numDivs = (endRow - startRow) * 2;
    var numHrs = numDivs - 1;
    var hrHeight = numHrs * 3;                  // 3 px
    var divHeight = parseInt(16.5 * numDivs);   // 16.5 px
    var height = hrHeight + divHeight;
    var event = '<div class="' + type + '" style="' +
            'height:' + height + 'px;width:' + div.width() + 'px;' +
            '"><a href="' + activity.url + '" target="_blank" class="link">' +
            activity.name + ' ' + activity.id + '</a></div>';
    div.html(event);
}

function displayTimetable(startTime, endTime, activityList, tableNum, container) {
    // make empty table for each term    
    var tableId1 = 'timetable' + tableNum + '-term1';
    var table1 = '<span style="display:inline-block;" id="' + tableId1 + '"><p>Term 1</p>';
    table1 += makeEmptyTable(startTime, endTime, 1);
    
    var tableId2 = 'timetable' + tableNum + '-term2';
    var table2 = '<span style="display:inline-block;" id="' + tableId2 + '"><p>Term 2</p>';
    table2 += makeEmptyTable(startTime, endTime, 2);
    
    table1 += '</span>';
    table2 += '</span>';
    var header = '<h5>Timetable ' + (tableNum + 1) + '</h5>'; 
    var table = '<div class="timetableDiv-outer">' + header + table1 + table2 + '</div>';
    
    // set/display the table
    container.html(container.html() + table);
    
    // populate the tables with activities
    var tableIds = [tableId1, tableId2];
    for (var i=0; i < activityList.length; i++) {
        var activity = activityList[i];
        var timeList = activity.times;
        
        for (var j=0; j < timeList.length; j++) {
            makeEvent(activity, timeList[j], startTime, tableIds);
        }
    }
}

function displayConflict(startTime, endTime, activityList, tableNum, container) {
    if (activityList[0].times[0]) {
        var term = activityList[0].times[0].term;
    } else {
        var term = "";
    }
    
    // make empty table for each activity involved in the conflict    
    var tableId1 = 'timetable' + tableNum + '-term1';
    var table1 = '<span style="display:inline-block;" id="' + tableId1 + '">';
    table1 += '<p>Term ' + term + '</p>';
    table1 += makeEmptyTable(startTime, endTime, 1);
    
    var tableId2 = 'timetable' + tableNum + '-term2';
    var table2 = '<span style="display:inline-block;" id="' + tableId2 + '">';
    table2 += '<p>Term ' + term + '</p>';
    table2 += makeEmptyTable(startTime, endTime, 2);
    
    table1 += '</span>';
    table2 += '</span>';
    var header = '<h5 style="color:rgb(185,48,48);">Conflict ' + (tableNum + 1) + '</h5>'; 
    var table = '<div class="timetableDiv-outer">' + header + table1 + table2 + '</div>';
    
    // set/display the table
    container.html(container.html() + table);
    
    // populate the tables with activities
    var tableIds = [tableId1, tableId2];
    var activity1 = activityList[0];
    var timeList = activity1.times;
    for (var j=0; j < timeList.length; j++) {
        makeEvent(activity1, timeList[j], startTime, tableIds, 1);
    }
    var activity2 = activityList[1];
    var timeList = activity2.times;
    for (var j=0; j < timeList.length; j++) {
        makeEvent(activity2, timeList[j], startTime, tableIds, 2);
    }
}

/**
 * Parses activities from xml response to create Activity class objects
 * @param xml activities 
 * @returns Array of [earliest start time, latest end time, Array of Activities]
 */
function getActivityList(activities) {
    var activityList = new Array();
    var lowTime = 25;
    var highTime = -1;

    for (var j=0; j < activities.length; j++) {
        var activity = activities[j];

        var name = activity.getAttribute('name');
        var id = activity.getAttribute('id');
        var type = activity.getAttribute('type');
        var term = activity.getAttribute('term');
        var url = activity.getAttribute('url');

        var times = activity.getElementsByTagName("time");
        var timesList = new Array();
        for (var k=0; k < times.length; k++) {
            var time = times[k];

            var start = time.getAttribute('start');
            var end = time.getAttribute('end');
            var startFloat = timeStringToFloat(start);
            var endFloat = timeStringToFloat(end);

            if (startFloat < lowTime) {
                lowTime = startFloat;
            }
            if (endFloat > highTime) {
                highTime = endFloat;
            }
            var day = time.getAttribute('day');
            var dayInt = dayStringToInt(day);
            var term = time.getAttribute('term');
            
            timesList[k] = new Time(startFloat, endFloat, dayInt, term);
        }
        activityList[j] = new Activity(name, timesList, id, type, url);
    }
    var startTime = parseInt(lowTime);
    var endTime = parseInt(highTime + 0.5);
    return [startTime, endTime, activityList];
}

function buildTimetables(xmlTimetables, pageNum) {
    var numPages = getNumPages(xmlTimetables.length);
    
    if (pageNum === undefined) {
        // no page specified, default to first page (page 0)
        pageNum = 0;
    } else if (pageNum < 0) {
        // request is for a page out of range, no nothing
        return;
    } else if (pageNum >= numPages) {
        // request is for a page out of range, no nothing
        return;
    } else if (pageNum === pageNumber) {
        // request is for the page currently being displayed. do nothing
        return;
    }
    // save the xml timetable data for later paging through
    storeTimetables(xmlTimetables, pageNum);
    
    var container = $('#timetable-container');
    // clear the timetable container
    container.html('');
    
    // first and last timetable to be displayed on this page
    var firstTT = pageNum * TIMETABLES_PER_PAGE;
    var lastTT = firstTT + TIMETABLES_PER_PAGE;
    
    for (var i=firstTT; i < xmlTimetables.length && i < lastTT; i++) {
        var activities = xmlTimetables[i].getElementsByTagName("activity");
        
        // parse list of activities from the xml
        var activityObj = getActivityList(activities);
        
        displayTimetable(activityObj[0], activityObj[1], activityObj[2], i, container);
    }
    // add paging buttons
    var pageButtons = getPageButtons(xmlTimetables.length, numPages, pageNum);
    container.html(pageButtons + container.html() + pageButtons);
    
    addTimetablePagesClickHandler();
}

function buildConflicts(xmlConflicts) {

    var container = $('#timetable-container');
    // clear the timetable container
    container.html('');
    
    for (var i=0; i < xmlConflicts.length; i++) {
        var activities = xmlConflicts[i].getElementsByTagName("activity");
        
        // parse list of activities from the xml
        var activityObj = getActivityList(activities);
        
        displayConflict(activityObj[0], activityObj[1], activityObj[2], i, container);
    }
}

function getNumPages(numTimetables) {
    var numPages = numTimetables / TIMETABLES_PER_PAGE;
    var numPagesInt = parseInt(numPages);
    
    if (numPages > numPagesInt) {
        return numPagesInt + 1;
    }
    return numPagesInt;
}

function getPageButtons(numTimetables, numPages, pageNum) {
    var pageButtons = '<div class="pagination"><ul><li><a class="timetablePage" href="">Prev</a></li>';
    
    for (var i=0; i < numPages; i++) {
        if (i === pageNum) {
            pageButtons += '<li class="timetablePage"><a class="timetablePage timetablePage-active" href="">' +
                    (i + 1) + '</a></li>';
        } else {
            pageButtons += '<li><a class="timetablePage" href="">' + (i + 1) + '</a></li>';
        }
    }
    pageButtons += '<li><a class="timetablePage" href="">Next</a></li></ul></div>';
    return pageButtons;
}

function storeTimetables(timetables, pageNum) {
    xmlTimetables = timetables;
    pageNumber = pageNum;
}

function addTimetablePagesClickHandler() {
    // bind to the submit event of our form
    $(".timetablePage").click(function(event) {
        // prevent default posting of form
        event.preventDefault();
        
        var buttonText = $(this).text();
        
        if (buttonText === "Next") {
            buildTimetables(xmlTimetables, pageNumber + 1);
        } else if (buttonText === "Prev") {
            buildTimetables(xmlTimetables, pageNumber - 1);
        } else {
            var pageNum = parseInt(buttonText);
            if (!isNaN(pageNum)) {
                buildTimetables(xmlTimetables, pageNum - 1);
            }
        }
    });
}