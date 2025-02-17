import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { useRef, useState, memo } from "react";

import { AfrHeader } from "../parts/AfrHeader";
import { LocaleSelector } from "../parts/LocaleSelector";
import { MessageAlert } from "../parts/MessageAlert";

type KcContext_UpdatePassword = Extract<KcContext, { pageId: "login-update-password.ftl" }>;

const UpdatePassword = memo((props: PageProps<KcContext_UpdatePassword, I18n>) => {
    const { kcContext, i18n, ...kcProps } = props;
    const { url, message, username, locale } = kcContext;
    const { msgStr, advancedMsgStr } = i18n;

    const [password, setPassword] = useState("");
    const [passwordError, setPasswordError] = useState(getError(kcContext, "password"));
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [passwordConfirmError, setPasswordConfirmError] = useState(getError(kcContext, "password-confirm"));

    const formRef = useRef<HTMLFormElement>(null);

    const onSubmit = () => {
        let hasError = false;

        if (!password.trim()) {
            setPasswordError("error-empty");
            hasError = true;
        }

        if (!passwordConfirm.trim()) {
            setPasswordConfirmError("error-empty");
            hasError = true;
        }

        if (password.trim() && passwordConfirm.trim() && password !== passwordConfirm) {
            setPasswordConfirmError("invalidPasswordConfirmMessage");
            hasError = true;
        }

        if (!hasError) {
            formRef.current?.submit();
        }
    };

    return (
        <div className="afr-login">
            <AfrHeader i18n={i18n} />

            <div className="afr-card">
                <header>
                    {locale && <LocaleSelector locale={locale} i18n={i18n} />}
                </header>

                <div className="afr-content mt-4">
                    <h2
                        className="text-2xl mx-auto font-bold text-slate-900"
                        style={{ marginTop: "40px", textAlign: "center" }}
                    >
                        {msgStr("updatePasswordTitle")}
                    </h2>

                    <MessageAlert message={message} i18n={i18n} />

                    <form
                        id="kc-update-password"
                        ref={formRef}
                        action={url.loginAction}
                        method="post"
                    >
                        <input
                            className="hidden"
                            id="username"
                            name="username"
                            type="hidden"
                            readOnly
                            value={username}
                            onChange={() => {}}
                        />

                        <div className={clsx(kcProps.kcFormGroupClass)}>
                            <label htmlFor="password">
                                {msgStr("passwordNew")}
                            </label>
                            <input
                                id="password"
                                name="password-new"
                                type="password"
                                value={password}
                                required
                                onChange={(e) => {
                                    setPassword(e.target.value);
                                    setPasswordError(undefined);
                                }}
                                style={{
                                    width: "90%",
                                    marginTop: "3%",
                                    padding: "0.75rem"
                                }}
                            />
                            {passwordError && (
                                <p className="text-red-500 text-sm">
                                    {advancedMsgStr(passwordError)}
                                </p>
                            )}
                        </div>

                        <div
                            className={clsx(kcProps.kcFormGroupClass)}
                            style={{ marginTop: "3%" }}
                        >
                            <label htmlFor="password-confirm">
                                {msgStr("passwordNewConfirm")}
                            </label>
                            <input
                                id="password-confirm"
                                name="password-confirm"
                                type="password"
                                value={passwordConfirm}
                                required
                                onChange={(e) => {
                                    setPasswordConfirm(e.target.value);
                                    setPasswordConfirmError(undefined);
                                }}
                                style={{
                                    width: "90%",
                                    marginTop: "3%",
                                    padding: "0.75rem"
                                }}
                            />
                            {passwordConfirmError && (
                                <p className="text-red-500 text-sm">
                                    {advancedMsgStr(passwordConfirmError)}
                                </p>
                            )}
                        </div>

                        <div className="afr-form-buttons">
                            <button
                                type="button"
                                className={clsx(kcProps.kcButtonClass, kcProps.kcButtonPrimaryClass)}
                                onClick={onSubmit}
                            >
                                {msgStr("updatePassword")}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
});

function getError(kcContext: KcContext_UpdatePassword, field: string): string | undefined {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch {
        return undefined;
    }
}

export default UpdatePassword;