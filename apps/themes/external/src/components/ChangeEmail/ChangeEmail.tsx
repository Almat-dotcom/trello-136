import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ChangeEvent, memo, useRef, useState } from "react";

type KcContext_ChangeEmail = Extract<KcContext, { pageId: "update-email.ftl" }>;

const ChangeEmail = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_ChangeEmail; i18n: I18n; } & KcProps) => {
    const { url, message, email } = kcContext;
    const {  msgStr, advancedMsgStr } = i18n;

    const [newEmail, setNewEmail] = useState(email || "");
    const [error, setError] = useState(getError(kcContext, "email") ?? undefined);

    const onEmailChanged = (event: ChangeEvent<HTMLInputElement>) => {
        setNewEmail(event.target.value);
    }

    const formRef = useRef<HTMLFormElement>(null);

    const onSubmit = () => {
        if (newEmail.length === 0 || newEmail.indexOf("@")) {
            setError("invalidEmailMessage");
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

                    <p className="my-6 text-slate-900 text-2xl font-bold">{msgStr("forcedToChangeEmail")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-reset-password-form" ref={formRef} action={url.loginAction} method="post">
                        <InputField
                            fieldName="email"
                            label={msgStr("email")}
                            type="text"
                            value={newEmail}
                            required
                            error={advancedMsgStr(error ?? "") ?? error}
                            onChange={onEmailChanged}
                            onEnter={onSubmit}
                        />

                        <div>
                            <Button severity="primary" type="button" onClick={onSubmit}>{msgStr("doSubmit")}</Button>
                        </div>
                    </form>
                </div>
            </div>
        </LayoutWithCarousel>
    );
});

const getError = (kcContext: KcContext_ChangeEmail, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch (error) {
        return undefined;
    }
}

export default ChangeEmail;
