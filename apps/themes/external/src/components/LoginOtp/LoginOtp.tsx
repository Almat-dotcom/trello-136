import React, { memo, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { KcContext } from "lib/kc";

type KcContextLoginOtp = Extract<KcContext, { pageId: "login-otp.ftl" }>;


const LoginOtp = memo(({ kcContext, i18n, ...props }: { kcContext: KcContextLoginOtp; i18n: I18n; } & KcProps) => {
    const { url, otpLogin, messagesPerField } = kcContext;
    const { msgStr } = i18n;
    const formRef = useRef<HTMLFormElement>(null);

    return (
        <div>
            <h1>{msgStr("doLogIn")}</h1>
            <form id="kc-otp-login-form" action={url.loginAction} ref={formRef} method="post">
               {otpLogin?.userOtpCredentials && Array.isArray(otpLogin.userOtpCredentials) && otpLogin.userOtpCredentials.length > 1 && (
                    <div>
                        {otpLogin.userOtpCredentials.map((otpCredential, index) => (
                            <div key={index}>
                                <input
                                    id={`kc-otp-credential-${index}`}
                                    type="radio"
                                    name="selectedCredentialId"
                                    value={otpCredential.id}
                                />
                                <label htmlFor={`kc-otp-credential-${index}`}>
                                    {otpCredential.userLabel}
                                </label>
                            </div>
                        ))}
                    </div>
                )}
                <div>
                    <label htmlFor="otp">{msgStr("loginOtpOneTime")}</label>
                    <input type="text" id="totp" name="totp" />
                     {messagesPerField.get('totp') && (
                            <span  aria-live="polite">
                                 {messagesPerField.get('totp')}
                             </span>
                        )}
                </div>
                <button type="button" onClick={() => formRef.current?.submit()}>{msgStr("doLogIn")}</button>
            </form>
        </div>
    );
});

export default LoginOtp;