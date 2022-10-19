<template>
    <div class="course-manger">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div v-if="courses.length > 0" class="table-responsive">
                        <table class="table table-striped table-sm">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th style="width: 90%">Summary</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr 
                                v-for="(course, index) in courses"
                                :key="course.id"
                            >
                                <td class="centered-count">{{index+1}}</td>
                                <td>
                                    <router-link
                                        class="ref-link"
                                        :to="{
                                            name: 'view-course', 
                                            params: { course_ref: course.course_ref}
                                        }"
                                    >
                                        <div class="student-summary mt-3 mb-3">
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-8">
                                                            <h4>{{course.title}} ({{course.code}})</h4>
                                                        </div>
                                                        <div class="col-4 ">
                                                        
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div 
                                                             v-if="getInstructorByRef(course.taught_by)" 
                                                            class="col-md-3"
                                                        >
                                                            By: {{instructor_fullname}} ({{course.taught_by}})
                                                        </div> 
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </router-link>
                                </td>
                            </tr>
                        </tbody>
                        </table>
                    </div>
                    <div v-else class="jumbotron">
                        <h4 style="text-align: center;">No Course Record Found.</h4>
                    </div>
                </main>
            </div>
        </div>
    </div>
</template>

<script>

    import db from '../firebase/init'
    import AdminSideBar from './AdminSidebar'
    import InnerHeader from './InnerHeader'

    export default {
        name: 'course-manger',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Course Manager",
               courses: [],
               instructors: [],
               instructor_fullname: ''
            }
        },

        created() {
            db.collection("courses").orderBy("title").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "course_ref":  doc.data().course_ref,
                        "title": doc.data().title,
                        "taught_by": doc.data().taught_by,
                        "code": doc.data().code
                    }

                    this.courses.push(data)
                })
            })

            db.collection("instructors").orderBy("fullname").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "instructor_ref":  doc.data().instructor_ref,
                        "fullname": doc.data().fullname,
                        "email": doc.data().email,
                        "device_ref": doc.data().device_ref,
                        "mobile": doc.data().mobile
                    }

                    this.instructors.push(data)
                })
            })
        },

        methods: {
            getInstructorByRef(instructorRef) {
                let selectedInstructor = null
                this.instructors.map(instructor => {
                    if (instructor.instructor_ref == instructorRef) {
                        selectedInstructor = instructor
                    }
                })
                
                if (selectedInstructor) {
                    this.instructor_fullname = selectedInstructor.fullname
                    return true;
                }
                this.instructor_fullname = ""
                return false;
            }
        }
    }
</script>

<style scoped>
    .centered-count {
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;

        font-weight: 900;
    }

    .ref-link {
        text-decoration: none;
        color: #000;
    }

    .ref-link:hover {
        color: #888;
    }
</style>