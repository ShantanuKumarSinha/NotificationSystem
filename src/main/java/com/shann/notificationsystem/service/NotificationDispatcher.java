package com.shann.notificationsystem.service;

import com.shann.notificationsystem.model.NotificationEvent;

public interface NotificationDispatcher {
    public void dispatch(NotificationEvent notificationEvent);
}
