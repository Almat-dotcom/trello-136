import React, { memo } from "react";
import { I18n } from "lib/i18n";
import { KcContextBase } from "keycloakify";


interface MessageAlertProps {
    message?: KcContextBase["message"];
    i18n: I18n;
}

export const MessageAlert = memo(({ message, i18n }: MessageAlertProps) => {
    if (!message) {
        return null;
    }

    const { advancedMsgStr } = i18n;
    return (
        <div className={`afr-message-container afr-message-container-${message.type}`}>
            <div className={`afr-message afr-message-${message.type}`}>
                {advancedMsgStr(message.summary)}
            </div>
        </div>
    );
});
