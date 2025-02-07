import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { ChangeEvent, useRef, useState, memo } from "react";

import { AfrHeader } from "../parts/AfrHeader";
import { LocaleSelector } from "../parts/LocaleSelector";
import { MessageAlert } from "../parts/MessageAlert";

type KcContext_ResetPassword = Extract<KcContext, { pageId: "login-reset-password.ftl" }>;

const ResetPassword = memo((props: PageProps<KcContext_ResetPassword, I18n>) => {
    const { kcContext, i18n, ...kcProps } = props;
    const { url, message, auth, locale } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    const [username, setUsername] = useState(auth?.attemptedUsername ?? "");
    const [error, setError] = useState<string | undefined>(
        getError(kcContext, "username") ?? undefined
    );

    const formRef = useRef<HTMLFormElement>(null);

    const onUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
        setError(undefined); 
    };

    const onSubmit = () => {
        if (username.trim().length === 0) {
            setError("error-empty");
            return;
        }
        formRef.current?.submit();
    };

    return (
        <div className="afr-login">
            <AfrHeader i18n={i18n} />
            <div className="afr-card">
                <header className="text-center">
                    {locale && <LocaleSelector locale={locale} i18n={i18n} />}
                </header>

                <div className="afr-content">
                    <h2
                        className="font-bold text-slate-900 text-center mb-4"
                        style={{ marginTop: "40px", textAlign: "center" }}
                    >
                        {msgStr("emailForgotTitle")}
                    </h2>

                    <MessageAlert message={message} i18n={i18n} />

                    <form
                        id="kc-reset-password-form"
                        ref={formRef}
                        action={url.loginAction}
                        method="post"
                        style={{ marginTop: "5%" }}
                    >
                        <div className={clsx(kcProps.kcFormGroupClass)} style={{ marginBottom: "1rem" }}>
                            <label htmlFor="username">
                                {msgStr("usernameOrEmail")}
                            </label>
                            <input
                                id="username"
                                name="username"
                                type="text"
                                value={username}
                                required
                                onChange={onUsernameChange}
                                style={{
                                    width: "90%",
                                    marginTop: "3%",
                                    padding: "0.75rem"
                                }}
                            />
                            {error && (
                                <p className="text-red-500 text-sm">
                                    {advancedMsgStr(error) ?? error}
                                </p>
                            )}
                        </div>

                        <p className="text-sm text-center text-gray-400 mt-4">
                            <a
                                href={url.loginUrl}
                                className="text-secondary-dark font-semibold underline"
                            >
                                {msg("backToLogin")}
                            </a>
                        </p>

                        <div className="afr-form-buttons text-center">
                            <button
                                type="button"
                                className={clsx(kcProps.kcButtonClass, kcProps.kcButtonPrimaryClass)}
                                onClick={onSubmit}
                                style={{
                                    padding: "0.5rem 1rem",
                                    borderRadius: "8px"
                                }}
                            >
                                {msgStr("doSubmit")}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
});

function getError(kcContext: KcContext_ResetPassword, field: string): string | undefined {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch {
        return undefined;
    }
}

export default ResetPassword;