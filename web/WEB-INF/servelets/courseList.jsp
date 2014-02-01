
<%@ page import="java.util.List" %>
<%@ page import="model.Course" %>

<div class="manageCourses-navbar">
    <div class="navbar navbar-inner">
        <div class="container-fluid">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <span class="brand">Manage Courses</span>
            <div class="nav-collapse collapse">
                
                <ul class="nav pull-right">
                    <!--li id="makeTimetables" class=""><a href="">Make Timetables</a></li-->
                    <li id="makeTimetables" class=""><button class="btn btn-info">Make Timetables</button></li>
                    <li class="divider-vertical"></li>
                    
                    <li class="dropdown preferencesDropdown">
                        <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="preferencesDropdown">Timetable Preferences</a>
                        <div class="dropdown-menu" style="padding: 15px; padding-bottom: 0px;"></div>
                    </li>
                    <li class="divider-vertical"></li>
                    
                    <!-- Add course menu -->
                    <li class="dropdown">
                        <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="addCourseDropdown">Add a Course<strong class="caret"></strong></a>
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
                        <a id="session-dropdown" class="dropdown-toggle" data-toggle="dropdown" href="#"></a>
                        <ul class="dropdown-menu" id="session-dropdown-menu"></ul>
                    </li>

                    <li class="divider-vertical"></li>

                    <!-- Campus dropdown menu -->
                    <li class="dropdown">
                        <a id="campus-dropdown" class="dropdown-toggle" data-toggle="dropdown" href="#">
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

<div class="container">
    <div id="preferencesPanel">
        <div class="well" style="width:205px; padding: 15px 0;">
            <h5 style="padding-left:15px;color: #6D6C6C;">Timetable Preferences</h5>
            <ul class="nav nav-list">
                <li><label class="tree-toggler nav-header">Days Off</label>
                    <ul class="nav nav-list tree">
                        <li><label class="tree-toggler nav-header">Term 1</label>
                            <ul class="nav nav-list tree dayoff-term1">
                                <li><a href="#">Monday</a></li>
                                <li><a href="#">Tuesday</a></li>
                                <li><a href="#">Wednesday</a></li>
                                <li><a href="#">Thursday</a></li>
                                <li><a href="#">Friday</a></li>
                            </ul>
                        </li>
                        <li><label class="tree-toggler nav-header">Term 2</label>
                            <ul class="nav nav-list tree dayoff-term2">
                                <li><a href="#">Monday</a></li>
                                <li><a href="#">Tuesday</a></li>
                                <li><a href="#">Wednesday</a></li>
                                <li><a href="#">Thursday</a></li>
                                <li><a href="#">Friday</a></li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li class="divider"></li>

                <li><label class="tree-toggler nav-header">Earliest Class</label>
                    <ul class="nav nav-list tree earliestClass">
                        <li><a href="">8:00</a></li>
                        <li><a href="">9:00</a></li>
                        <li><a href="">10:00</a></li>
                        <li><a href="">11:00</a></li>
                        <li><a href="">12:00</a></li>
                        <li><a href="">13:00</a></li>
                        <li><a href="">14:00</a></li>
                        <li><a href="">15:00</a></li>
                    </ul>
                </li>
                <li class="divider"></li>

                <li><label class="tree-toggler nav-header">Latest Class</label>
                    <ul class="nav nav-list tree latestClass">
                        <li><a href="">10:00</a></li>
                        <li><a href="">11:00</a></li>
                        <li><a href="">12:00</a></li>
                        <li><a href="">13:00</a></li>
                        <li><a href="">14:00</a></li>
                        <li><a href="">15:00</a></li>
                        <li><a href="">16:00</a></li>
                        <li><a href="">17:00</a></li>
                        <li><a href="">18:00</a></li>
                        <li><a href="">19:00</a></li>
                        <li><a href="">20:00</a></li>
                    </ul>
                </li>
                <li class="divider"></li>

                <li><label class="tree-toggler nav-header">Time Between Classes</label>
                    <ul class="nav nav-list tree breakTime">
                        <li><a href="">As little as possible</a></li>
                        <li><a href="">1 Hour</a></li>
                        <li><a href="">2 Hours</a></li>
                        <li><a href="">3 Hours</a></li>
                        <li><a href="">4 Hours</a></li>
                        <li><a href="">5 Hours</a></li>
                        <li><a href="">As much as possible</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>

    <%!
        public Boolean isEmpty(List list) {
            return list == null || list.isEmpty();
        }
    %>

    <!-- Add course error container -->
    <div id="addCourseError"></div>
    
    <div id="courses-table">
        <div class="info" style="opacity:0.6;">
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
            <table class="table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Course Name</th>
                        <th>Term</th>
                        <th>Username</th>
                        <th></th>
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
                        <td> <a href="<% out.print(course.getUrl());%>" target="_blank" class="link"><% out.print(course.getCourseName());%></a></td>
                        <td> <% out.print(course.getTerm());%> </td>
                        <td>[username]</td>
                        <td></td>
                        <td>
                            <a href="removeCourse?course=<% out.print(course.getCourseName());%>" role="button" data-toggle="modal">
                                <i class="icon-remove" title="Remove Course"></i>
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
    
    <div class="loading-modal"><!-- Loading animation modal, Place at bottom of page --></div>