import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { ChangeEvent, useRef, useState, memo } from "react";

type KcContext_ResetPassword = Extract<KcContext, { pageId: "login-reset-password.ftl" }>;

const ResetPassword = memo((props: PageProps<KcContext_ResetPassword, I18n>) => {
    const { kcContext, i18n, Template, ...kcProps } = props;
    const { url, message, auth } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    const [username, setUsername] = useState(auth?.attemptedUsername ?? "");
    const [error, setError] = useState(getError(kcContext, "username") ?? undefined);

    const formRef = useRef<HTMLFormElement>(null);

    const onUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    };

    const onSubmit = () => {
        if (username.length === 0) {
            setError("error-empty");
            return;
        }
        formRef.current?.submit();
    };

    return (
        <div className="afr-login">
            <div id="kc-header" className="afr-header">
                <div id="kc-header-wrapper" className="afr-header-wrapper">
                    <span>AFR</span>
                </div>
            </div>
            <div className="afr-card">
                <header className=" text-center">
                    <h2 className="font-bold text-slate-900">{msgStr("emailForgotTitle")}</h2>
                    {message && (
                        <div className={`afr-message-container afr-message-container-${message.type}`}>
                            <div className={`afr-message afr-message-${message.type}`}>
                                {advancedMsgStr(message.summary)}
                            </div>
                        </div>
                    )}
                </header>
                <div className="afr-content mt-4">
                    <form id="kc-reset-password-form" ref={formRef} action={url.loginAction} method="post">
                        <div className={clsx(kcProps.kcFormGroupClass)}>
                            <label htmlFor="username" className={clsx(kcProps.kcLabelClass)}>
                                {msgStr("usernameOrEmail")}
                            </label>
                            <input
                                id="username"
                                name="username"
                                type="text"
                                className={clsx(kcProps.kcInputClass)}
                                value={username}
                                required
                                onChange={onUsernameChange}
                            />
                            {error && <p className="text-red-500 text-sm">{advancedMsgStr(error) ?? error}</p>}
                        </div>
                        <div className="afr-form-buttons">
                            <button type="button" className={clsx(kcProps.kcButtonClass, kcProps.kcButtonPrimaryClass)} onClick={onSubmit}>
                                {msgStr("doSubmit")}
                            </button>
                        </div>
                        <p className="text-sm text-center text-gray-400 mt-6 mb-6">
                            <a href={url.loginUrl} className="text-secondary-dark font-semibold underline">
                                {msg("backToLogin")}
                            </a>
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
});

const getError = (kcContext: KcContext_ResetPassword, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch {
        return undefined;
    }
};

export default ResetPassword;