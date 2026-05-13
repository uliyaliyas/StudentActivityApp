const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onNewTask = onDocumentCreated("tasks/{taskId}", async (event) => {
    const task = event.data.data();
    if (!task || !task.title) return;

    await admin.messaging().send({
        notification: {
            title: "Новое задание!",
            body: task.title
        },
        topic: "students"
    });
});

exports.onNewReward = onDocumentCreated("rewards/{rewardId}", async (event) => {
    const reward = event.data.data();
    if (!reward || !reward.title) return;

    await admin.messaging().send({
        notification: {
            title: "Новая награда!",
            body: `${reward.title} за ${reward.points} баллов`
        },
        topic: "students"
    });
});
