<template>
    <div class="view-student">
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
                                <th style="width: 250px" scope="row">Grade</th>
                                <td>{{grade}}</td>
                            </tr> 
                            <tr>
                                <th style="width: 250px" scope="row">Guardian</th>
                                <td>{{guardian}}</td>
                            </tr> 
                            <tr>
                                <th style="width: 250px" scope="row">Guardian Mobile</th>
                                <td>{{guardian_mobile}}</td>
                            </tr>                          
                        </tbody>
                    </table>
                    <div class="row">
                        <router-link class="btn btn-secondary ml-3" to="/admin-dashboard/student-manager">Back</router-link>
                        <router-link 
                            class="btn btn-warning ml-3" 
                            :to="{
                                name: 'edit-student',
                                params: { student_ref: student_ref }
                            }"
                        >
                            Edit
                        </router-link>
                        <button @click="deleteStudentRecord" class="btn btn-danger ml-3">Delete</button>
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
        name: 'view-student',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               student_ref: "",
               fullname: "",
               email: "",
               grade: null,
               guardian: "",
               guardian_mobile: "",
               mobile: "",
               classMemberShip: {},
               studentCourseList: [],
               timetable: null,

               weekDays: ["sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"],
            }
        },

        beforeRouteEnter(to, from, next) {
            db.collection("students").where('student_ref', '==', to.params.student_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        next(vm => {
                            vm.student_ref = doc.data().student_ref
                            vm.fullname = doc.data().fullname
                            vm.email = doc.data().email
                            vm.grade = doc.data().grade
                            vm.guardian = doc.data().guardian
                            vm.guardian_mobile = doc.data().guardian_mobile
                            vm.mobile = doc.data().mobile
                        })
                    })
                })
        }, 

        watch: {
            '$route': 'fetchData',

            timetable: function (val){
                if (val) {
                    val.timetable[this.weekDays[new Date().getDay()]].map((itemOnTimetable) => {
                        if (itemOnTimetable.course_ref !== "") {
                            this.studentCourseList.push(itemOnTimetable)
                        }
                    })
                }
            }
        }, 

        computed: {
            pageHeader() {
                return this.fullname + " (" + this.student_ref + ")"
            }
        },

        methods: {
            fetchData() {
                db.collection("students").where('student_ref', '==', this.$route.params.student_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.student_ref = doc.data().student_ref
                            this.fullname = doc.data().fullname
                            this.email = doc.data().email
                            this.grade = doc.data().grade
                            this.guardian = doc.data().guardian
                            this.guardian_mobile = doc.data().guardian_mobile
                            this.mobile = doc.data().mobile
                        })
                    })
            },

            deleteStudentRecord() {
                if (confirm("Are you sure?")) {
                    db.collection("students").where('student_ref', '==', this.student_ref).get()
                        .then( querySnapshot => {
                            querySnapshot.forEach( doc => {
                                doc.ref.delete()
                                this.$router.push("/admin-dashboard/student-manager")
                            })
                        })
                }
            },

            getStudentCourseList() {
               console.log("OK")
            }
        },

        created() {
            let retrieveAppUsers = new Promise((resolve, reject) => {
                db.collection("classroom_members").where('student_ref', '==', this.$route.params.student_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        this.classMemberShip.id = doc.id
                        this.classMemberShip.student_ref =  doc.data().student_ref
                        this.classMemberShip.classroom_ref = doc.data().classroom_ref
                    })
                    resolve(true)
                })   
            })

            let emitEvents = new Promise(async  (resolve, reject) => {
                await retrieveAppUsers;
                db.collection("time_tables").where('classroom_ref', '==', this.classMemberShip.classroom_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        this.timetable = doc.data().timetable
                    })
                })
                resolve(true)
            })

        },
    }
</script>

<style scoped>
    
</style>