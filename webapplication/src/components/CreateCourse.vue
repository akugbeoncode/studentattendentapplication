<template>
    <div class="create-course">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="col-md-6 order-md-1 offset-md-3">
                        <form>
                            <div class="mb-3">
                                <label for="email">Course Ref. No</label>
                                <input type="text" v-model="course_ref" class="form-control" placeholder="saa-cos-001" required>
                            </div>

                            <div class="mb-3">
                                <label for="email">Title</label>
                                <input type="text" v-model="title" class="form-control" placeholder="Biology" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Code</label>
                                <input type="text" v-model="code" class="form-control" placeholder="BIO" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Taught By</label>
                                <select class="form-control" v-model="taught_by">
                                    <option value="">Select Instructor</option>
                                    <option v-for="instructor in instructors" :key="instructor.id" :value="instructor.instructor_ref">{{instructor.fullname}} ({{instructor.instructor_ref}})</option>
                                </select>
                            </div>

                            <hr class="mb-4">
                            <button  :disabled="loading" @click="createCourseRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Create Course Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
                            </button>
                        </form>
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
        name: 'create-course',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                pageHeader: "Course Manager/Add",
                course_ref: "",
                title: "",
                code: "",
                taught_by: "",
                instructors: [],

                loading: false
            }
        },

        methods: {
            createCourseRecord (e) {
                this.loading = true
                e.preventDefault()
                db.collection("courses").add({
                    "course_ref":  this.course_ref,
                    "title": this.title,
                    "code": this.code,
                    "taught_by": this.taught_by
                }).then( docRef => {
                    alert("Course Record created successfully.")
                    this.$router.push("/admin-dashboard/course-manager")
                    this.loading = false
                })
                .catch( error => {
                    console.log(error)
                    this.loading = false 
                })
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