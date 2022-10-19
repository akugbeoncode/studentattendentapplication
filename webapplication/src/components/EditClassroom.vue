<template>
    <div class="edit-classroom">
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
                            <button  :disabled="loading" @click="updateClassroomRecord" class="btn btn-primary btn-lg btn-block" type="button">
                                Update Classroom Record <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
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
        name: 'edit-classroom',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader:"Classroom Manager/Edit",
               classroom_ref: "",
               label: "",
               code: "",
               capacity: null,

                loading: false
            }
        },

        beforeRouteEnter(to, from, next) {
            db.collection("classrooms").where('classroom_ref', '==', to.params.classroom_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        next(vm => {
                            vm.classroom_ref = doc.data().classroom_ref
                            vm.label = doc.data().label
                            vm.code = doc.data().code
                            vm.capacity = doc.data().capacity
                        })
                    })
                })
        }, 

        watch: {
            '$route': 'fetchData'
        }, 

        methods: {
            fetchData() {
                db.collection("classrooms").where('classroom_ref', '==', this.$route.params.classroom_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.classroom_ref = doc.data().classroom_ref
                            this.label = doc.data().label
                            this.code = doc.data().code
                            this.capcity = doc.data().capcity
                        })
                    })
            },

            updateClassroomRecord() {
                this.loading = true
                db.collection("classrooms").where('classroom_ref', '==', this.$route.params.classroom_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            doc.ref.update({
                                "classroom_ref":  this.classroom_ref,
                                "label": this.label,
                                "code": this.code,
                                "capacity": this.capacity
                            }).then(() => {
                                    this.$router.push({
                                    name: 'view-classroom',
                                    params: { classroom_ref: this.classroom_ref }
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