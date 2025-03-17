<template>
    <div>
        <el-container>
            <el-header class="help-header"><h1>Help Center</h1></el-header>
            <el-container>
                <el-aside width="250px">
                    <el-menu default-active="1" class="el-menu-vertical" mode="vertical" @select="handleSelect">
                        <el-menu-item index="1">
                            <i class="el-icon-wallet"></i>
                            <span>Account</span>
                        </el-menu-item>
                        <el-menu-item index="2">
                            <i class="el-icon-tickets"></i>
                            <span>Billing & Subscriptions</span>
                        </el-menu-item>
                        <el-submenu index="3">
                            <template slot="title">
                                <i class="el-icon-question"></i>
                                <span>Help Center</span>
                            </template>
                            <el-menu-item index="3-1" @click="navigateToFAQ">
                                <i class="el-icon-document"></i>
                                <span>FAQ</span>
                            </el-menu-item>
                            <el-menu-item index="3-2">
                                <i class="el-icon-service"></i>
                                <span>Live Support</span>
                            </el-menu-item>
                            <el-menu-item index="3-3">
                                <i class="el-icon-chat-line-square"></i>
                                <span>Feedback</span>
                            </el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-main>
                    <!-- Rest of the component content -->
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
export default {
    name: 'HelpCenter',
    data() {
        return {
            activeIndex: '1',
            helpDocs: [
                // ... existing code ...
            ],
            feedbackForm: {
                // ... existing code ...
            }
        }
    },
    methods: {
        handleSelect(key) {
            this.activeIndex = key;
        },
        navigateToFAQ() {
            this.$router.push('/faq');
        },
        submitFeedback() {
            // ... existing code ...
        }
    }
}
</script> 