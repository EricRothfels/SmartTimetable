
<%@ page import="java.util.List" %>
<%@ page import="model.Course" %>

<div class="container">
<div class="manageCourses-navbar">
    <div class="navbar navbar-inner">
        <div class="container-fluid" style="padding-left: 0px; padding-right: 0px;">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <span class="brand">Manage Courses</span>
            <div class="nav-collapse collapse">
                
                <ul class="nav pull-right">
                    <!--li id="makeTimetables" class=""><a href="">Make Timetables</a></li-->
                    <li id="makeTimetables" class=""><button class="btn btn-info"><i class="icon-play"></i> Make Timetables</button></li>
                    <li class="divider-vertical"></li>
                    
                    <li class="dropdown preferencesDropdown">
                        <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="preferencesDropdown"><i class="icon-tasks"></i> Timetable Preferences</a>
                        <div class="dropdown-menu" style="padding: 15px; padding-bottom: 0px;"></div>
                    </li>
                    <li class="divider-vertical"></li>
                    
                    <!-- Add course menu -->
                    <li class="dropdown">
                        <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="addCourseDropdown"><i class="icon-plus"></i> Add a Course<strong class="caret"></strong></a>
                        <div class="dropdown-menu" style="padding: 15px; padding-bottom: 0px;">
                            <form id="addCourseForm">
                                <input id="course-input" style="margin-bottom: 15px;" type="text" placeholder="Course Code eg. Apsc 201" name="courseInput">
                                <input class="btn btn-primary btn-block" type="submit" value="Add Course">
                            </form>
                        </div>
                    </li>

                    <li class="divider-vertical"></li>

                    <!-- Session dropdown menu -->
                    <li class="dropdown">
                        <a id="session-dropdown" class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-time"></i></a>
                        <ul class="dropdown-menu" id="session-dropdown-menu"></ul>
                    </li>

                    <li class="divider-vertical"></li>

                    <!-- Campus dropdown menu -->
                    <li class="dropdown">
                        <a id="campus-dropdown" class="dropdown-toggle" data-toggle="dropdown" href="#">
                            <i class="icon-flag"></i>
                            <jsp:scriptlet>
                                String campus = (String) session.getAttribute("campus");
                                if (campus == null) {
                                    campus = "UBC";
                                    session.setAttribute("campus", campus);
                                    out.print("Campus: Vancouver ");
                                } else if (campus.equals("UBC")) {
                                    out.print("Campus: Vancouver ");
                                } else {
                                    out.print("Campus: Okanagan ");
                                }
                            </jsp:scriptlet>
                            <strong class="caret"></strong>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="" id="campusOption1">Vancouver</a></li>
                            <li><a href="" id="campusOption2">Okanagan</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
        <!--/.container-fluid -->
    </div>
    <!--/.navbar-inner -->
</div>


    <!-- Add course error container -->
    <div id="addCourseError"></div>
    
    <div id="preferencesPanel">
        <div class="well">
            <h5 style="padding-left:15px;color: #6D6C6C;">Timetable Preferences <i class="icon-remove" id="removePrefPanel"></i></h5>
            <table>
                <tr>
                    <td><label class="tree-toggler nav-header">Days Off</label>
                        <ul class="nav nav-list tree">
                            <li><label class="tree-toggler nav-header">Term 1</label>
                                <form>
                                    <label><input type="checkbox" name="dayoffTerm1" class="preferencesInput" value="Monday"> Monday</label>
                                    <label><input type="checkbox" name="dayoffTerm1" class="preferencesInput" value="Tuesday"> Tuesday</label>
                                    <label><input type="checkbox" name="dayoffTerm1" class="preferencesInput" value="Wednesday"> Wednesday</label>
                                    <label><input type="checkbox" name="dayoffTerm1" class="preferencesInput" value="Thursday"> Thursday</label>
                                    <label><input type="checkbox" name="dayoffTerm1" class="preferencesInput" value="Friday"> Friday</label>
                                </form>
                            </li>
                            <li><label class="tree-toggler nav-header">Term 2</label>
                                <form>
                                    <label><input type="checkbox" name="dayoffTerm2" class="preferencesInput" value="Monday"> Monday</label>
                                    <label><input type="checkbox" name="dayoffTerm2" class="preferencesInput" value="Tuesday"> Tuesday</label>
                                    <label><input type="checkbox" name="dayoffTerm2" class="preferencesInput" value="Wednesday"> Wednesday</label>
                                    <label><input type="checkbox" name="dayoffTerm2" class="preferencesInput" value="Thursday"> Thursday</label>
                                    <label><input type="checkbox" name="dayoffTerm2" class="preferencesInput" value="Friday"> Friday</label>
                                </form>
                            </li>
                        </ul>
                    </td>
                    <td>

                        <label class="tree-toggler nav-header">Earliest Class</label>
                        <ul class="nav nav-list tree">
                            <li>
                                <form>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="-1" checked="checked"> No Preference</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="8"> 8:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="9"> 9:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="10"> 10:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="11"> 11:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="12"> 12:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="13"> 13:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="14"> 14:00</label>
                                    <label><input type="radio" name="earliestClass" class="preferencesInput" value="15"> 15:00</label>
                                </form>
                            </li>
                        </ul>
                    </td>

                    <td><label class="tree-toggler nav-header">Latest Class</label>
                        <ul class="nav nav-list tree">
                            <li>
                                <form>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="-1" checked="checked"> No Preference</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="10"> 10:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="11"> 11:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="12"> 12:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="13"> 13:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="14"> 14:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="15"> 15:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="16"> 16:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="17"> 17:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="18"> 18:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="19"> 19:00</label>
                                    <label><input type="radio" name="latestClass" class="preferencesInput" value="20"> 20:00</label>
                                </form>
                            </li>
                        </ul>
                    </td>

                    <td><label class="tree-toggler nav-header">Time Between Classes <span style="color:rgb(28, 88, 147);">(Not yet Implemented)</span></label>
                        <ul class="nav nav-list tree">
                            <li>
                                <form>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="-1" checked="checked"> No Preference</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="0"> As little as possible</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="1"> 1 Hour</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="2"> 2 Hours</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="3"> 3 Hours</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="4"> 4 Hours</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="5"> 5 Hours</label>
                                    <label><input type="radio" name="breakTime" class="preferencesInput" value="12"> As much as possible</label>
                                </form>
                            </li>
                        </ul>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <%!
        public Boolean isEmpty(List list) {
            return list == null || list.isEmpty();
        }
    %>

    <div id="courses-table">
        <div class="info" style="opacity:0.7;">
        <%  List<Course> courseList = (List<Course>) session.getAttribute("courseList");

            Boolean courseListIsEmpty = isEmpty(courseList); //courseList == null || courseList.isEmpty();

            if (courseListIsEmpty) {
        %>
                <div class="alert fade in">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <strong style="color:#7A4D00;">You have not added any courses yet.</strong> Try adding some courses!
                </div>
        </div>
        <%  } else {
        %>
        </div>
        <div class="btn-toolbar">
            <button class="btn btn-info">Save this List</button>
            <button class="btn btn-info">Import Saved List</button>
        </div>
        <div class="well">
            <a style="float:right;" href="removeCourse?course=REMOVE_ALL" role="button" data-toggle="modal">
                <i class="icon-remove" title="Remove All Courses"></i>
            </a>
            <table class="table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th></th>
                        <th>Course Name</th>
                        <th>Term</th>
                        <th>Username</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody id="courses-table-body">
                    <%
                        int i = 0;
                        for (Course course : courseList) {
                            i++;
                    %>
                    <tr>
                        <td> <% out.print(i);%> </td>
                        <td></td>
                        <td><a href="<% out.print(course.getUrl());%>" target="_blank" class="link">
                                <% out.print(course.getCourseName());%>
                            </a>
                        </td>
                        <td> <% out.print(course.getTerm());%> </td>
                        <td>[username]</td>
                        <td>
                            <a href="removeCourse?course=<% out.print(course.getCourseName());%>" role="button" data-toggle="modal">
                                <i class="icon-remove" title="Remove <% out.print(course.getCourseName()); %>"></i>
                            </a>
                        </td>
                    </tr>
                    <%
                        }
                    %>

                </tbody>
            </table>
        </div>
        <%
            }
        %>
    </div>
    
    <!-- div to hold the timetables result -->
    <div id="timetable-container"></div>
    

    <!-- Validate Add course input textbox -->
    <script src="js/jquery.validate.js" type="text/javascript"></script>
    <!--script src="http://twitter.github.com/bootstrap/assets/js/bootstrap-tooltip.js"></script-->

    <script type="text/javascript">

        $(document).ready(function() {
            // set current page to active css
            $('#courseList').attr("class", "active");
            
            // init tool-tip text
            $(".icon-remove").tooltip({
                'selector': '',
                'placement': 'bottom'
            });
            
            // init ajax handlers
            initPreferencesHandler();
            
            // Populate Session Drop-down menu with sessions
            initSessionDropdown();
            
            // init course input validation
            validateAddCourseForm();
            
            // init ajax request handlers
            initCourseSubmitHandler();
            initChangeCampusHandler();
            initMakeTimetableHandler();
        });
    </script>
    
    <div class="loading-modal"><div class="loading-modal-background"></div><!-- Loading animation modal, Place at bottom of page --></div>