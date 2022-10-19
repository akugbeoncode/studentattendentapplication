<template>
    <div class="create-student">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="col-md-6 order-md-1 offset-md-3">
                        <form>
                            <div class="mb-3">
                                <label for="email">Student Ref. No</label>
                                <input type="text" v-model="student_ref" class="form-control" placeholder="saa-2020-001" required>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="firstName">First name</label>
                                    <input type="text" class="form-control" v-model="firstname" placeholder="John" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="lastName">Last name</label>
                                    <input type="text" v-model="lastname" class="form-control" placeholder="Doe" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="email">Email</label>
                                <input type="email" v-model="email" class="form-control" placeholder="you@example.com" required>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="firstName">Mobile</label>
                                    <input type="text" v-model="mobile" class="form-control" placeholder="08037384755" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="lastName">Grade</label>
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
                            <button  :disabled="loading" @click="createStudentRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Create Student Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
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
    import firebase from 'firebase'
    import AdminSideBar from './AdminSidebar'
    import InnerHeader from './InnerHeader'

    import passwordHash from '../helpers/hash'

    export default {
        name: '',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                pageHeader:"Student Manager/Add",
                student_ref: "",
                firstname: "",
                lastname: "",
                email: "",
                mobile: "",
                grade: null,
                guardian: "",
                guardian_mobile: "",

                loading: false
            }
        },

        methods: {
            createStudentRecord (e) {
                this.loading = true
                e.preventDefault()
                const password = this.generateStudentPassword()
                firebase
                    .auth()
                    .createUserWithEmailAndPassword(this.email, password)
                    .then( response => {
                        db.collection("students").add({
                            "student_ref":  this.student_ref,
                            "fullname": this.lastname + " " + this.firstname,
                            "email": this.email,
                            "grade": this.grade,
                            "guardian": this.guardian,
                            "guardian_mobile": this.guardian_mobile,
                            "mobile": this.mobile,
                            "password": password
                        }).then( docRef => {
                            // send email to student
                            alert("Student Record created successfully.")
                            this.$router.push("/admin-dashboard/student-manager")
                            this.loading = false
                        })
                        .catch( error => {
                            console.log(error)
                            this.loading = false 
                        })
                    },
                    err => alert(err.message))
            },

            generateStudentPassword() {
                let length = 8,
                    charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&",
                    token = "";
                for (let i = 0, n = charset.length; i < length; ++i) {
                    token += charset.charAt(Math.floor(Math.random() * n));
                }
                return token;
            }

        }
    }
</script>

<style scoped>
    
</style>