import { memo, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { Layout } from "components/Layout";
import { InputField, InputSelect } from "components/parts/Input";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { KcContext_Registration, useRegisterPage } from "./hooks";
import PdfModal from "components/parts/PdfModal";
import PaRu from "./pa_ru.md";
import PaKz from "./pa_kz.md";
import PaEn from "./pa_en.md";
import LkRu from "./lk_ru.md";
import LkKz from "./lk_kz.md";
import LkEn from "./lk_en.md";

const Registration = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Registration; i18n: I18n; } & KcProps) => {
    const { url, message } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    const formRef = useRef<HTMLFormElement>(null);

    const { fields, legal, resident, buttonDisabled, concents, onSubmit } = useRegisterPage(kcContext, () => formRef.current?.submit())

    const error = (key?: string) => key ? advancedMsgStr(key) ?? key : undefined

    const collectingConsent = (lang?: string) => {
        if (lang === "ru") {
            return PaRu;
        }
        if (lang === "kz") {
            return PaKz;
        }
        return PaEn;
    }

    const privateConsent = (lang?: string) => {
        if (lang === "ru") {
            return LkRu;
        }
        if (lang === "kz") {
            return LkKz;
        }
        return LkEn;
    }

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <form ref={formRef} id="kc-register-form" action={url.registrationAction} method="post">
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 mb-4 text-slate-900 text-2xl font-bold">{msgStr("registerTitle")}</p>
                </div>

                <InputSelect
                    fieldName="residency"
                    label={msgStr("residency")}
                    options={[{
                        value: "resident",
                        label: msgStr("resident")
                    }, {
                        value: "non-resident",
                        label: msgStr("nonResident")
                    }]}
                    value={fields.residency.value}
                    error={error(fields.residency.error)}
                    onValueChange={(option) => { fields.residency.onChange(option.value) }}
                />
                <InputSelect
                    fieldName="clientType"
                    label={msgStr("clientType")}
                    options={[{
                        value: "physical",
                        label: msgStr("physical")
                    }, {
                        value: "legal",
                        label: msgStr("legal")
                    }]}
                    value={fields.clientType.value}
                    error={error(fields.clientType.error)}
                    onValueChange={(option) => { fields.clientType.onChange(option.value) }}
                />
                {legal ? (
                    <InputSelect
                        fieldName="legalRole"
                        label={msgStr("legalRole")}
                        options={[{
                            value: "head",
                            label: msgStr("head")
                        }, {
                            value: "employee",
                            label: msgStr("employee")
                        }]}
                        value={fields.legalRole.value}
                        error={error(fields.legalRole.error)}
                        onValueChange={(option) => fields.legalRole.onChange(option.value)}
                    />
                ) : null}
                <InputField
                    fieldName="lastName"
                    label={msgStr("lastName")}
                    type="text"
                    value={fields.lastName.value}
                    error={error(fields.lastName.error)}
                    onChange={(event) => fields.lastName.onChange(event.target.value)}
                />
                <InputField
                    fieldName="firstName"
                    label={msgStr("firstName")}
                    type="text"
                    value={fields.firstName.value}
                    error={error(fields.firstName.error)}
                    onChange={(event => { fields.firstName.onChange(event.target.value) })}
                />
                <InputField
                    fieldName="middleName"
                    label={msgStr("middleName")}
                    type="text"
                    value={fields.middleName.value}
                    error={error(fields.middleName.error)}
                    onChange={(event) => { fields.middleName.onChange(event.target.value) }}
                />
                <InputField
                    fieldName="email"
                    label={msgStr("email")}
                    type="text"
                    value={fields.email.value}
                    error={error(fields.email.error)}
                    onChange={(event) => { fields.email.onChange(event.target.value) }}
                />
                {legal && resident ? (
                    <InputField
                        fieldName="bin"
                        label={msgStr("bin")}
                        type="text"
                        value={fields.bin.value}
                        error={error(fields.bin.error)}
                        maxLength={12}
                        onChange={(event) => fields.bin.onChange(event.target.value)}
                    />
                ) : null}
                {resident ? (
                    <InputField
                        fieldName="iin"
                        label={msgStr("iin")}
                        type="text"
                        value={fields.iin.value}
                        error={error(fields.iin.error)}
                        maxLength={12}
                        onChange={(event) => fields.iin.onChange(event.target.value)}
                    />
                ) : null}
                <InputField
                    fieldName="password"
                    label={msgStr("password")}
                    type="password"
                    value={fields.password.value}
                    error={error(fields.password.error)}
                    onChange={(event) => fields.password.onChange(event.target.value)}
                />
                <InputField
                    fieldName="password-confirm"
                    label={msgStr("passwordConfirm")}
                    type="password"
                    value={fields.passwordConfirm.value}
                    error={error(fields.passwordConfirm.error)}
                    onChange={(event) => fields.passwordConfirm.onChange(event.target.value)}
                />

                <PdfModal
                    kcContext={kcContext}
                    url={collectingConsent(kcContext.locale?.currentLanguageTag)}
                    shown={concents.concent1Shown}
                    onClosed={concents.tuggleConcent1}
                />
                <div className="flex flex-row items-start justify-start mt-4">
                    <div className="flex items-center h-5">
                        <input
                            id="contentCollectConsent"
                            name="contentCollectConsent"
                            type="checkbox"
                            checked={concents.concent1}
                            onChange={concents.onConcent1Clicked}
                            className="bg-gray-50 border-gray-300 accent-primary  focus:ring-3 focus:ring-primary h-4 w-4 rounded"
                        />
                    </div>
                    <div className="text-sm ml-3">
                        <button type="button" className="bg-none border-none font-medium text-start text-blue-500 hover:underline" onClick={concents.tuggleConcent1}>
                            {msgStr("concentOnCollectingData")}
                        </button>
                    </div>
                </div>

                <PdfModal kcContext={kcContext} url={privateConsent(kcContext.locale?.currentLanguageTag)} shown={concents.concent2Shown} onClosed={concents.tuggleConcent2} />
                <div className="flex flex-row items-start justify-start mt-4">
                    <div className="flex items-center h-5">
                        <input
                            id="lkConsent"
                            name="lkConsent"
                            type="checkbox"
                            checked={concents.concent2}
                            onChange={concents.onConcent2Clicked}
                            className="bg-gray-50 border-gray-300 accent-primary  focus:ring-3 focus:ring-primary h-4 w-4 rounded"
                        />
                    </div>
                    <div className="text-sm ml-3">
                        <button type="button" className="bg-none border-none font-medium text-start text-blue-500 hover:underline" onClick={concents.tuggleConcent2}>
                            {msgStr("concentLK")}
                        </button>
                    </div>
                </div>

                <Button severity="primary" type="button" disabled={buttonDisabled} onClick={() => onSubmit()}>{msgStr("doRegister")}</Button>
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
        </Layout>
    )
})

export default Registration
