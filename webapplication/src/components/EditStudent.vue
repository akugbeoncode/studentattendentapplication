<template>
    <div class="edit-student">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="col-md-6 order-md-1 offset-md-3">
                        <!-- <h4 class="mb-3">Billing address</h4> -->
                        <form>
                            <div class="mb-3">
                                <label for="email">Student Ref. No</label>
                                <input disabled type="text" v-model="student_ref" class="form-control" placeholder="saa-2020-001" required>
                            </div>

                            <div class="mb-3">
                                <label>Fullname</label>
                                <input type="text" v-model="fullname" class="form-control" placeholder="John Doe" required>
                            </div>

                            <div class="mb-3">
                                <label for="email">Email</label>
                                <input type="email" v-model="email" class="form-control" placeholder="you@example.com" required>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label >Mobile</label>
                                    <input type="text" v-model="mobile" class="form-control" placeholder="08037384755" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label>Grade</label>
                                    <input type="text" v-model="grade" class="form-control" placeholder="2" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Guardian Fullname</label>
                                <input type="text" v-model="guardian" class="form-control" placeholder="Jerry Horner" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Guardian Mobile</label>
                                <input type="text" v-model="guardian_mobile" class="form-control" placeholder="08037384755" required>
                            </div>

                            <hr class="mb-4">
                            <button  :disabled="loading" @click="updateStudentRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Update Student Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
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
        name: 'edit-student',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                pageHeader:"Student Manager/Edit",
                student_ref: "",
                fullname:"",
                email: "",
                mobile: "",
                grade: null,
                guardian: "",
                guardian_mobile: "",
                fullname: '',

                loading: false
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
            '$route': 'fetchData'
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

            updateStudentRecord() {
                this.loading = true
                db.collection("students").where('student_ref', '==', this.$route.params.student_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            doc.ref.update({
                                "student_ref":  this.student_ref,
                                "fullname": this.fullname,
                                "email": this.email,
                                "grade": this.grade,
                                "guardian": this.guardian,
                                "guardian_mobile": this.guardian_mobile,
                                "mobile": this.mobile
                            }).then(() => {
                                    this.$router.push({
                                    name: 'view-student',
                                    params: { student_ref: this.student_ref }
                                })
                                this.loading = false
                            })
                        })
                    })
            }
        }
    }
</script>

<style scoped>
    
</style>