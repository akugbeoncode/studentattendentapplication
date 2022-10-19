<template>
    <div class="edit-instructor">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="col-md-6 order-md-1 offset-md-3">
                        <form>
                            <div class="mb-3">
                                <label for="email">Instructor Ref. No</label>
                                <input type="text" disabled v-model="instructor_ref" class="form-control" placeholder="saa-inst-00001" required>
                            </div>

                            <div class="mb-3">
                                <label>Fullname</label>
                                <input type="text" v-model="fullname" class="form-control" placeholder="John Doe" required>
                            </div>

                            <div class="mb-3">
                                <label for="email">Email</label>
                                <input type="email" v-model="email" class="form-control" placeholder="you@example.com" required>
                            </div>

                            <div class="mb-3">
                                <label for="firstName">Mobile</label>
                                <input type="text" v-model="mobile" class="form-control" placeholder="08037384755" required>
                            </div>

                            <hr class="mb-4">
                            <button  :disabled="loading" @click="updateInstructorRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Update Instructor Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
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
        name: 'edit-instructor',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                pageHeader:"Instructor Manager/Edit",
                instructor_ref: '',
                fullname: '',
                email: '',
                mobile: '',

                loading: false
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
                db.collection("instructors").where('instructor_ref', '==', this.$route.params.instructor_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.instructor_ref = doc.data().instructor_ref
                            this.fullname = doc.data().fullname
                            this.email = doc.data().email
                            this.mobile = doc.data().mobile
                        })
                    })
            },

            updateInstructorRecord() {
                this.loading = true
                db.collection("instructors").where('instructor_ref', '==', this.$route.params.instructor_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            doc.ref.update({
                                "instructor_ref":  this.instructor_ref,
                                "fullname": this.fullname,
                                "email": this.email,
                                "mobile": this.mobile
                            }).then(() => {
                                    this.$router.push({
                                    name: 'view-instructor',
                                    params: { instructor_ref: this.instructor_ref }
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