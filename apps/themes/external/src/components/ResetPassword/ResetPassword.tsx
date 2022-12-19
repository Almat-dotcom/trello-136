import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ChangeEvent, memo, useRef, useState } from "react";

type KcContext_ResetPassword = Extract<KcContext, { pageId: "login-reset-password.ftl" }>;

const ResetPassword = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_ResetPassword; i18n: I18n; } & KcProps) => {
    const { url, message, auth } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    const [username, setUsername] = useState(auth?.attemptedUsername ?? "");
    const [error, setError] = useState(getError(kcContext, "username") ?? undefined);

    const onUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    }

    const formRef = useRef<HTMLFormElement>(null);

    const onSubmit = () => {
        if (username.length === 0) {
            setError("error-empty");
            return;
        }

        formRef.current?.submit();
    }

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("emailForgotTitle")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-reset-password-form" ref={formRef} action={url.loginAction} method="post">
                        <InputField
                            fieldName="username"
                            label={msgStr("usernameOrEmail")}
                            type="text"
                            value={username}
                            error={advancedMsgStr(error ?? "") ?? error}
                            onChange={onUsernameChange}
                        />

                        <div>
                            <Button severity="primary" type="button" onClick={() => onSubmit()}>{msgStr("doSubmit")}</Button>
                        </div>

                        <p
                            className="mt-6 mb-6 text-sm text-center text-gray-400"
                        >
                            <a
                                href={url.loginUrl}
                                className="ml-4 text-blue-500 hover:text-primary focus:outline-none focus:underline hover:underline"
                            >
                                {msg("backToLogin")}
                            </a>
                        </p>
                    </form>
                </div>
            </div>
        </LayoutWithCarousel>
    );
});

const getError = (kcContext: KcContext_ResetPassword, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch (error) {
        return undefined;
    }
}

export default ResetPassword;
