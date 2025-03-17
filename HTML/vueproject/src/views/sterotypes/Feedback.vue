<template>
    <div>
        <el-container>
            <el-header class="help-header"><h1>Help Center</h1></el-header>
            <el-container>
                <el-aside width="250px">
                    <el-menu default-active="4-3" class="el-menu-vertical" mode="vertical">
                        <el-menu-item index="1">
                            <i class="el-icon-wallet"></i>
                            <span>Account</span>
                        </el-menu-item>
                        <el-menu-item index="2">
                            <i class="el-icon-tickets"></i>
                            <span>Billing & Subscriptions</span>
                        </el-menu-item>
                        <el-menu-item index="3">
                            <i class="el-icon-money"></i>
                            <span>Budget</span>
                        </el-menu-item>
                        <el-submenu index="4">
                            <template slot="title">
                                <i class="el-icon-question"></i>
                                <span>Help Center</span>
                            </template>
                            <el-menu-item index="4-1" v-on:click="goToFAQ">
                                <i class="el-icon-document"></i>
                                <span>FAQ</span>
                            </el-menu-item>
                            <el-menu-item index="4-2" v-on:click="goToLiveSupport">
                                <i class="el-icon-service"></i>
                                <span>Live Support</span>
                            </el-menu-item>
                            <el-menu-item index="4-3">
                                <i class="el-icon-chat-line-square"></i>
                                <span>Feedback</span>
                            </el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-main>
                    <h2>Feedback</h2>
                    <el-form :model="feedbackForm" label-width="120px">
                        <el-form-item label="Feedback Type">
                            <el-select v-model="feedbackForm.type" placeholder="Select feedback type">
                                <el-option label="Feature Request" value="feature"></el-option>
                                <el-option label="Bug Report" value="bug"></el-option>
                                <el-option label="AI Improvement" value="ai"></el-option>
                                <el-option label="Other" value="other"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="Subject">
                            <el-input v-model="feedbackForm.title" placeholder="Enter a subject"></el-input>
                        </el-form-item>
                        <el-form-item label="Message">
                            <el-input type="textarea" v-model="feedbackForm.content" rows="5" placeholder="Please describe your feedback in detail"></el-input>
                        </el-form-item>
                        <el-form-item label="Contact Info">
                            <el-input v-model="feedbackForm.contact" placeholder="Leave your contact information for us to follow up"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="submitFeedback">Submit Feedback</el-button>
                        </el-form-item>
                    </el-form>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
export default {
    name: 'FeedbackView',
    data() {
        return {
            feedbackForm: {
                type: '',
                title: '',
                content: '',
                contact: ''
            }
        }
    },
    methods: {
        goToFAQ() {
            this.$router.push('/faq');
        },
        goToLiveSupport() {
            this.$router.push('/livesupport');
        },
        submitFeedback() {
            // Logic for submitting feedback
            this.$message({
                message: 'Thank you for your feedback! We will review it shortly.',
                type: 'success'
            });
            // Reset form
            this.feedbackForm = {
                type: '',
                title: '',
                content: '',
                contact: ''
            };
        }
    }
}
</script>   

<style scoped>
.el-header {
    background: linear-gradient(135deg, #3a8ee6, #5ca1ff);
    color: white;
    text-align: center;
    line-height: 60px;
    padding: 0;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.help-header h1 {
    margin: 0;
    font-weight: 500;
    font-size: 24px;
    letter-spacing: 1px;
}

.el-aside {
    background-color: #f8f8f8;
    border-right: 1px solid #e6e6e6;
    min-width: 250px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.el-menu-vertical {
    border-right: none;
    width: 100%;
}

.el-menu-item span {
    white-space: normal;
    line-height: 1.2;
    padding: 10px 0;
}

.el-submenu .el-menu-item span {
    white-space: normal;
    line-height: 1.2;
}

.doc-card {
    margin-bottom: 15px;
    transition: all 0.3s;
    border-radius: 8px;
}

.doc-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-main {
    padding: 20px 30px;
}

.el-collapse-item {
    margin-bottom: 10px;
    border-radius: 4px;
    overflow: hidden;
}
</style> 