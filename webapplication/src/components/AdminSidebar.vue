<template>
    <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse">
        <div class="sidebar-sticky pt-5">
            <router-link class="anchor" to="/admin-dashboard">Dashboard</router-link>
            <button @click="toggleCategoryExpansion(categories[0])" class="dropdown-btn">{{categories[0]}}
                <i class="fa fa-caret-down"></i>
            </button>
            <div 
                class="dropdown-container"
                :class="{
                    'dropdown-container': !isStudentCategoryExpanded,
                    'dropdown-container-expanded': isStudentCategoryExpanded
                }"
            >
                <router-link class="anchor" to="/admin-dashboard/student-manager">Students</router-link>
                <router-link class="anchor" to="/admin-dashboard/student-manager/add">Create</router-link>
            </div>
            <button @click="toggleCategoryExpansion(categories[1])" class="dropdown-btn">{{categories[1]}}
                <i class="fa fa-caret-down"></i>
            </button>
            <div
                class="dropdown-container"
                :class="{
                    'dropdown-container': !isInstructorCategoryExpanded,
                    'dropdown-container-expanded': isInstructorCategoryExpanded
                }"
            >
                <router-link class="anchor" to="/admin-dashboard/instructor-manager">Instructors</router-link>
                <router-link class="anchor" to="/admin-dashboard/instructor-manager/add">Create</router-link>
            </div>
            <button @click="toggleCategoryExpansion(categories[2])" class="dropdown-btn">{{categories[2]}}
                <i class="fa fa-caret-down"></i>
            </button>
            <div 
                class="dropdown-container"
                :class="{
                    'dropdown-container': !isClassroomCategoryExpanded,
                    'dropdown-container-expanded': isClassroomCategoryExpanded
                }"
            >
                <router-link class="anchor" to="/admin-dashboard/classroom-manager">Classrooms</router-link>
                <router-link class="anchor" to="/admin-dashboard/classroom-manager/add">Create</router-link>
            </div>
            <button @click="toggleCategoryExpansion(categories[3])" class="dropdown-btn">{{categories[3]}}
                <i class="fa fa-caret-down"></i>
            </button>
            <div
                class="dropdown-container"
                :class="{
                    'dropdown-container': !isCourseCategoryExpanded,
                    'dropdown-container-expanded': isCourseCategoryExpanded
                }"
            >
                <router-link class="anchor" to="/admin-dashboard/course-manager">Courses</router-link>
                <router-link class="anchor" to="/admin-dashboard/course-manager/add">Create</router-link>
            </div>
            <button @click="toggleCategoryExpansion(categories[4])" class="dropdown-btn">{{categories[4]}}
                <i class="fa fa-caret-down"></i>
            </button>
            <div
                class="dropdown-container"
                :class="{
                    'dropdown-container': !isReportCategoryExpanded,
                    'dropdown-container-expanded': isReportCategoryExpanded
                }"
            >
                <router-link class="anchor" to="/admin-dashboard/report-manager/attendance">Attendants</router-link>
                <router-link class="anchor" to="/admin-dashboard/report-manager/quiz">Quiz</router-link>
            </div>
        </div>
    </nav>
</template>

<script>
    export default {
        name: 'admin-sidebar',
        data() {
            return {
                categories: [
                    'Student Manager',
                    'Instructor Manager',
                    'Classroom Manager',
                    'Course Manager',
                    'Report Manager'
                ],
                isStudentCategoryExpanded: true,
                isInstructorCategoryExpanded: false,
                isClassroomCategoryExpanded: false,
                isCourseCategoryExpanded: false,
                isReportCategoryExpanded: false,
            }
        },

        methods: {
            toggleCategoryExpansion(targetCategory) {
                if (targetCategory == this.categories[0]) {
                    this.isStudentCategoryExpanded = !this.isStudentCategoryExpanded
                } else if (targetCategory == this.categories[1]) {
                    this.isInstructorCategoryExpanded = !this.isInstructorCategoryExpanded
                } else if (targetCategory == this.categories[2]) {
                    this.isClassroomCategoryExpanded = !this.isClassroomCategoryExpanded
                } else if (targetCategory == this.categories[3]) {
                    this.isCourseCategoryExpanded = !this.isCourseCategoryExpanded
                } else if (targetCategory == this.categories[4]) {
                    this.isReportCategoryExpanded = !this.isReportCategoryExpanded
                }
            }
        }
    }
</script>


<style scoped>
    .admin-sidebar {
        height: 100%;
        min-height: 100vh;

        overflow-y: scroll;
        overflow-x: hidden !important;
    }

    .anchor, .dropdown-btn {
        padding: 8px 8px 8px 16px;
        text-decoration: none;
        font-size: 13px;
        color: #000;
        display: block;
        border: none;
        background: none;
        width:100%;
        text-align: left;
        cursor: pointer;
        outline: none;
    }

    .dropdown-btn {
        border-bottom: 1px solid #ddd;
        font-weight: 700;
    }

    .anchor:hover, .dropdown-btn:hover {
        color: #444;
    }

    .active {
        background-color: green;
        color: white;
    }

    .dropdown-container {
        display: none;
        background-color: #f9f9f9;
        padding-left: 8px;
    }

    .dropdown-container-expanded {
        display: block;
        background-color: #ccc;
        padding-left: 8px;
    }

    .fa-caret-down {
        float: right;
        padding-right: 8px;
    }
</style>