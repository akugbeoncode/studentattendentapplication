<template>
    <div class="view-course">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th style="width: 250px" scope="row">Taught By</th>
                                <td>{{fullname}} ({{taught_by}})</td>
                            </tr>                          
                        </tbody>
                    </table>
                    <div class="row">
                        <router-link class="btn btn-secondary ml-3" to="/admin-dashboard/course-manager">Back</router-link>
                        <router-link 
                            class="btn btn-warning ml-3" 
                            :to="{
                                name: 'edit-course',
                                params: { course_ref: course_ref }
                            }"
                        >
                            Edit
                        </router-link>
                        <button @click="deleteCourseRecord" class="btn btn-danger ml-3">Delete</button>
                    </div>
                    <!-- <h4 class="h3 mt-5">Course List</h4> -->
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
        name: 'view-course',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                course_ref: "",
                title: "",
                code: "",
                taught_by: "",
                instructors: []
            }
        },

        beforeRouteEnter(to, from, next) {
            db.collection("courses").where('course_ref', '==', to.params.course_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        next(vm => {
                            vm.course_ref = doc.data().course_ref
                            vm.title = doc.data().title
                            vm.code = doc.data().code
                            vm.taught_by = doc.data().taught_by
                        })
                    })
                })
        }, 

        watch: {
            '$route': 'fetchData'
        }, 

        computed: {
            pageHeader() {
                return this.title + " (" + this.code + ")"
            },

            fullname() {
                let instr = this.getInstructorByRef(this.taught_by)
                if (instr) {
                    return instr.fullname
                }
                return ""
            }
        },

        methods: {
            fetchData() {
                db.collection("courses").where('course_ref', '==', this.$route.params.course_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.course_ref = doc.data().course_ref
                            this.title = doc.data().title
                            this.code = doc.data().code
                            this.taught_by = doc.data().taught_by
                        })
                    })
            },

            deleteCourseRecord() {
                if (confirm("Are you sure?")) {
                    db.collection("courses").where('course_ref', '==', this.course_ref).get()
                        .then( querySnapshot => {
                            querySnapshot.forEach( doc => {
                                doc.ref.delete()
                                this.$router.push("/admin-dashboard/course-manager")
                            })
                        })
                }
            },

            getInstructorByRef(instructorRef) {
                let selectedInstructor = null
                this.instructors.map(instructor => {
                    if (instructor.instructor_ref == instructorRef) {
                        selectedInstructor = instructor
                    }
                })
                
                return selectedInstructor
            }
        },

        created() {
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
        }
    }
</script>

<style scoped>
    
</style>