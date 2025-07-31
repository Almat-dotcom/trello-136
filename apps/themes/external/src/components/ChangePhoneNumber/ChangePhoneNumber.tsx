import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import InputPhoneWithCountry from "components/parts/Input/InputPhoneWithCountry";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo, useRef, useState } from "react";

type KcContext_ChangePhoneNumber = Extract<KcContext, { pageId: "update-phone.ftl" }>;

const ChangePhoneNumber = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_ChangePhoneNumber; i18n: I18n; } & KcProps) => {
    const { url, message, phoneNumber } = kcContext;
    const { msgStr, advancedMsgStr } = i18n;

    const [newPhoneNumber, setNewPhoneNumber] = useState(phoneNumber || "");
    const [error, setError] = useState(getError(kcContext, "phoneNumber") ?? undefined);

    const onPhoneNumberChanged = (value: string) => {
        setNewPhoneNumber(value);
    }

    const formRef = useRef<HTMLFormElement>(null);

    const onSubmit = () => {
        if (newPhoneNumber.length === 0) {
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

                    <p className="my-6 text-slate-900 text-2xl font-bold">{msgStr("forcedToChangePhoneNumber")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-reset-password-form" ref={formRef} action={url.loginAction} method="post">

                        <InputPhoneWithCountry
                            fieldName="phoneNumber"
                            label={msgStr("phoneNumber")}
                            placeholder="+x (xxx) xxx xx xx"
                            required
                            value={newPhoneNumber}
                            error={advancedMsgStr(error ?? "") ?? error}
                            onChange={(value) => onPhoneNumberChanged(value)}
                            isNonResident={true}
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

const getError = (kcContext: KcContext_ChangePhoneNumber, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch (error) {
        return undefined;
    }
}

export default ChangePhoneNumber;
