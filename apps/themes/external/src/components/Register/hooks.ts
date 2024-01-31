import { KcContext } from "lib/kc";
import { signAuthXml } from "lib/ncalayer";
import { CancelledByUser, ConnectionLost } from "lib/ncalayer/NCALayer";
import { RefObject, useState } from "react";

export type KcContext_Registration = Extract<KcContext, { pageId: "register.ftl" }>;

type Field = {
    value: string,
    error?: string,
    validate: () => boolean
    onChange: (value: string) => void
}

type RegisterFields = {
    residency: Field,
    clientType: Field,
    lastName: Field,
    firstName: Field,
    middleName: Field,
    email: Field,
    iin: Field,
    phoneNumber: Field,
    password: Field,
    passwordConfirm: Field
}

type Concents = {
    concent1: boolean,
    concent1Shown: boolean,
    tuggleConcent1: () => void,
    onConcent1Clicked: () => void,
    concent2: boolean,
    concent2Shown: boolean,
    tuggleConcent2: () => void,
    onConcent2Clicked: () => void
}

type RegisterForm = {
    message?: {
        type: "success" | "warning" | "error" | "info",
        summary: string
    },
    fields: RegisterFields,
    legal: boolean,
    resident: boolean,
    buttonDisabled: boolean,
    concents: Concents,
    onSubmit: () => Promise<void>
}

export const useRegisterPage = (
    kcContext: KcContext_Registration,
    edsRef: RefObject<HTMLInputElement>,
    onFormSubmit: () => void
): RegisterForm => {
    const [signing, setSigning] = useState(false);
    const [message, setMessage] = useState(kcContext.message);
    const [legal, setLegal] = useState(kcContext.register.formData.clientType === 'legal');
    const [resident, setResident] = useState(kcContext.register.formData.residency === 'resident');
    const [concent1, setConcent1] = useState(false);
    const [concent1Shown, setConcent1Shown] = useState(false);
    const [concent2, setConcent2] = useState(false);
    const [concent2Shown, setConcent2Shown] = useState(false);
    const fields = useRegisterFields(kcContext, legal, (value) => setLegal(value), resident, (value) => setResident(value));
    return {
        message: message,
        fields: fields,
        legal: legal,
        resident: resident,
        buttonDisabled: signing || !concent1 || !concent2,
        concents: {
            concent1: concent1,
            concent1Shown: concent1Shown,
            tuggleConcent1: function () { setConcent1Shown(!concent1Shown) },
            onConcent1Clicked: function () { setConcent1(!concent1) },
            concent2: concent2,
            concent2Shown: concent2Shown,
            tuggleConcent2: function () { setConcent2Shown(!concent2Shown) },
            onConcent2Clicked: function () { setConcent2(!concent2) },
        },
        onSubmit: async function () {
            const residencyValid = fields.residency.validate();
            const typeValid = fields.clientType.validate();
            const lastValid = fields.lastName.validate();
            const firstValid = fields.firstName.validate();
            const middleValid = fields.middleName.validate();
            const emailValid = fields.email.validate();
            const iinValid = fields.iin.validate();
            const passwordValid = fields.password.validate();
            const confirmValid = fields.passwordConfirm.validate();
            const phoneNumberValid = fields.phoneNumber.validate();

            if (residencyValid && typeValid && lastValid && firstValid && middleValid && emailValid && iinValid && passwordValid && confirmValid && phoneNumberValid) {

                if (legal) {
                    setSigning(true);
                    try {
                        const xml = '<registration></registration>'
                        setMessage({ type: 'info', summary: 'ncaSignProgress' });
                        const result = await signAuthXml(xml);
                        edsRef.current!.value = result;
                        setMessage({ type: 'success', summary: 'ncaSignFinished' });
                        onFormSubmit();
                    } catch (error) {
                        console.error(error);
                        if (error === ConnectionLost) {
                            setMessage({ type: 'error', summary: 'ncaConnectionLost' });
                        } else if (error === CancelledByUser) {
                            setMessage({ type: 'error', summary: 'ncaCancelled' });
                        } else {
                            setMessage({ type: 'error', summary: 'ncaFailed' });
                        }
                    }
                    setSigning(false);
                } else {
                    onFormSubmit();
                }
            }
        }
    }
}

const useRegisterFields = (
    kcContext: KcContext_Registration,
    legal: boolean,
    onLegalChanged: (legal: boolean) => void,
    resident: boolean,
    onResidentChanged: (resident: boolean) => void
): RegisterFields => {
    const { register } = kcContext;
    const { formData } = register;
    const { residency, clientType, lastName, firstName, middleName, email, phoneNumber, iin } = formData;
    return {
        residency: useField(isNotEmpty, (value) => { onResidentChanged(value === 'resident') }, residency, extractError(kcContext, "residency")),
        clientType: useField(isNotEmpty, (value) => { onLegalChanged(value === 'legal') }, clientType, extractError(kcContext, "clientType")),
        lastName: useField(isNotEmptyNorLegal(() => legal), () => { }, lastName, extractError(kcContext, "lastName")),
        firstName: useField(isNotEmptyNorLegal(() => legal), () => { }, firstName, extractError(kcContext, "firstName")),
        middleName: useField(allAllowed, () => { }, middleName, extractError(kcContext, "middleName")),
        email: useField(isNotEmpty, () => { }, email, extractError(kcContext, "email")),
        iin: useField(isBinIinOnResident(() => resident, () => legal), () => { }, iin, extractError(kcContext, "iin")),
        phoneNumber: useField(isNotEmpty, () => { }, phoneNumber, extractError(kcContext, "phoneNumber")),
        password: useField(isNotEmpty, () => { }, undefined, extractError(kcContext, "password")),
        passwordConfirm: useField(isNotEmpty, () => { }, undefined, extractError(kcContext, "password-confirm"))
    };
}

const useField = (validator: (value: string) => string | undefined, changeCallBack: (value: string) => void, initialValue?: string, errorSource?: string | undefined): Field => {
    const [value, setValue] = useState(initialValue ?? "")
    const [error, setError] = useState(errorSource)
    return {
        value: value,
        error: error,
        validate: () => {
            const result = validator(value);
            if (result) {
                setError(result);
            }
            return !result;
        },
        onChange: function (newValue) {
            if (newValue !== value) {
                setValue(newValue);
                changeCallBack(newValue)
            }
        }
    }
}

const isNotEmptyNorLegal = (legal: () => boolean) =>
    (value: string): string | undefined => legal() ? undefined : isNotEmpty(value);

const isBinIinOnResident = (resident: () => boolean, legal: () => boolean) =>
    (value: string): string | undefined => resident() && !legal() ? isNotEmpty(value) || is12CharsAndNumeric(value) : undefined;

const is12CharsAndNumeric = (value: string): string | undefined => value.length !== 12 || isNaN(+value) ? "only12Digits" : undefined;

const isNotEmpty = (value: string): string | undefined => !value ? "error-empty" : undefined;

const allAllowed = (value: string): string | undefined => undefined;

const extractError = (kcContext: KcContext_Registration, fieldName: string): string | undefined => {
    const { messagesPerField } = kcContext;
    try {
        if (messagesPerField.exists(fieldName) && messagesPerField.existsError(fieldName)) {
            return messagesPerField.get(fieldName);
        }
    } catch (error) {
        return undefined;
    }
    return undefined;
}
