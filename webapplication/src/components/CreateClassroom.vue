<template>
    <div class="create-classroom">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="col-md-6 order-md-1 offset-md-3">
                        <form>
                            <div class="mb-3">
                                <label for="email">Classroom Ref. No</label>
                                <input type="text" v-model="classroom_ref" class="form-control" placeholder="saa-cls-001" required>
                            </div>

                            <div class="mb-3">
                                <label for="email">Label</label>
                                <input type="text" v-model="label" class="form-control" placeholder="Excellence Hall" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Code</label>
                                <input type="text" v-model="code" class="form-control" placeholder="EH" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Capacity</label>
                                <input type="text" v-model="capacity" class="form-control" placeholder="20" required>
                            </div>

                            <hr class="mb-4">
                            <button  :disabled="loading" @click="createClassroomRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Create Classroom Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
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
        name: 'create-classroom',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader:"Classroom Manager/Add",
               classroom_ref: "",
               label: "",
               code: "",
               capacity: null,

                loading: false
            }
        },

        methods: {
            createClassroomRecord (e) {
                this.loading = true
                e.preventDefault()
                db.collection("classrooms").add({
                    "classroom_ref":  this.classroom_ref,
                    "label": this.label,
                    "code": this.code,
                    "capacity": this.capacity
                }).then( docRef => {
                    // send email to student
                    alert("Classroom Record created successfully.")
                    this.$router.push("/admin-dashboard/classroom-manager")
                    this.loading = false
                })
                .catch( error => {
                    console.log(error)
                    this.loading = false 
                })
            }
        }
    }
</script>

<style scoped>
    
</style>