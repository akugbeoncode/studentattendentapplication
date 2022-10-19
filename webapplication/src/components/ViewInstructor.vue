<template>
    <div class="view-instructor">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th style="width: 250px" scope="row">Email</th>
                                <td>{{email}}</td>
                            </tr> 
                            <tr>
                                <th style="width: 250px" scope="row">Mobile</th>
                                <td>{{mobile}}</td>
                            </tr> 
                            <tr>
                                <th style="width: 250px" scope="row">Device Code</th>
                                <td>{{device_code}}</td>
                            </tr>                          
                        </tbody>
                    </table>
                    <div class="row">
                        <router-link class="btn btn-secondary ml-3" to="/admin-dashboard/instructor-manager">Back</router-link>
                        <router-link 
                            class="btn btn-warning ml-3" 
                            :to="{
                                name: 'edit-instructor',
                                params: { instructor_ref: instructor_ref }
                            }"
                        >
                            Edit
                        </router-link>
                        <button @click="deleteInstructorRecord" class="btn btn-danger ml-3">Delete</button>
                    </div>
                    <h4 class="h3 mt-5">Course List</h4>
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
        name: 'view-instructor',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                instructor_ref: "",
                fullname: "",
                email: "",
                device_code: "",
                mobile: ""
            }
        },

        beforeRouteEnter(to, from, next) {
            db.collection("instructors").where('instructor_ref', '==', to.params.instructor_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        next(vm => {
                            vm.instructor_ref = doc.data().instructor_ref
                            vm.fullname = doc.data().fullname
                            vm.email = doc.data().email
                            vm.device_code = doc.data().device_code
                            vm.mobile = doc.data().mobile
                        })
                    })
                })
        }, 

        watch: {
            '$route': 'fetchData'
        }, 

        computed: {
            pageHeader() {
                return this.fullname + " (" + this.instructor_ref + ")"
            }
        },

        methods: {
            fetchData() {
                db.collection("instructors").where('instructor_ref', '==', this.$route.params.instructor_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.instructor_ref = doc.data().instructor_ref
                            this.fullname = doc.data().fullname
                            this.email = doc.data().email
                            this.device_code = doc.data().device_code
                            this.mobile = doc.data().mobile
                        })
                    })
            },

            deleteInstructorRecord() {
                if (confirm("Are you sure?")) {
                    db.collection("instructors").where('instructor_ref', '==', this.instructor_ref).get()
                        .then( querySnapshot => {
                            querySnapshot.forEach( doc => {
                                doc.ref.delete()
                                this.$router.push("/admin-dashboard/instructor-manager")
                            })
                        })
                }
            }
        }
    }
</script>

<style scoped>
    
</style>