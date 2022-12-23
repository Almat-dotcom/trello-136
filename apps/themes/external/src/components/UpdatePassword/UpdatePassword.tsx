import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { memo, useRef } from "react";
import { KcContext_UpdatePassword, useUpdatePasswordForm } from "./hooks";

const UpdatePassword = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_UpdatePassword; i18n: I18n; } & KcProps) => {
    const { url, message, username } = kcContext;
    const { msgStr, advancedMsgStr } = i18n;

    const formRef = useRef<HTMLFormElement>(null);

    const {
        password,
        passwordError,
        onPasswordChange,
        passwordConfirm,
        passwordConfirmError,
        onConfirmChange,
        onSubmit
    } = useUpdatePasswordForm(kcContext, () => formRef.current?.submit());

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("updatePasswordTitle")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-update-password" ref={formRef} action={url.loginAction} method="post">
                        <input className="hidden" id="username" name="username" type="text" readOnly value={username} onChange={() => { }} />
                        <input className="hidden" id="password" name="password" type="password"/>
                        <InputField
                            fieldName="password-new"
                            label={msgStr("passwordNew")}
                            type="password"
                            value={password}
                            error={advancedMsgStr(passwordError ?? "") ?? passwordError}
                            onChange={(event) => onPasswordChange(event.target.value)}
                            onEnter={onSubmit}
                        />
                        <InputField
                            fieldName="password-confirm"
                            label={msgStr("passwordNewConfirm")}
                            type="password"
                            value={passwordConfirm}
                            error={advancedMsgStr(passwordConfirmError ?? "") ?? passwordConfirmError}
                            onChange={(event) => onConfirmChange(event.target.value)}
                            onEnter={onSubmit}
                        />

                        <div>
                            <Button type="button" severity="primary" onClick={onSubmit}>{msgStr("doSubmit")}</Button>
                        </div>
                    </form>
                </div>
            </div>
        </LayoutWithCarousel>
    );
})

export default UpdatePassword;
