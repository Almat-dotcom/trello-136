import { KcContext } from "lib/kc";
import { useState } from "react";

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
    legalRole: Field,
    lastName: Field,
    firstName: Field,
    middleName: Field,
    email: Field,
    bin: Field,
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
    fields: RegisterFields,
    legal: boolean,
    resident: boolean,
    head: boolean,
    buttonDisabled: boolean,
    concents: Concents,
    onSubmit: () => void
}

export const useRegisterPage = (kcContext: KcContext_Registration, onFormSubmit: () => void): RegisterForm => {
    const [legal, setLegal] = useState(kcContext.register.formData.clientType === 'legal');
    const [resident, setResident] = useState(kcContext.register.formData.residency === 'resident');
    const [head, setHead] = useState(kcContext.register.formData.legalRole === "head");
    const [concent1, setConcent1] = useState(false);
    const [concent1Shown, setConcent1Shown] = useState(false);
    const [concent2, setConcent2] = useState(false);
    const [concent2Shown, setConcent2Shown] = useState(false);
    const fields = useRegisterFields(kcContext, legal, (value) => setLegal(value), resident, (value) => setResident(value), head, (value) => setHead(value));
    return {
        fields: fields,
        legal: legal,
        resident: resident,
        head: head,
        buttonDisabled: !concent1 || !concent2,
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
        onSubmit: function () {
            const residencyValid = fields.residency.validate();
            const typeValid = fields.clientType.validate();
            const legalValid = fields.legalRole.validate();
            const lastValid = fields.lastName.validate();
            const firstValid = fields.firstName.validate();
            const middleValid = fields.middleName.validate();
            const emailValid = fields.email.validate();
            const binValid = fields.bin.validate();
            const iinValid = fields.iin.validate();
            const passwordValid = fields.password.validate();
            const confirmValid = fields.passwordConfirm.validate();
            const phoneNumberValid = fields.phoneNumber.validate();

            if (residencyValid && typeValid && legalValid && lastValid && firstValid && middleValid && emailValid && binValid && iinValid && passwordValid && confirmValid && phoneNumberValid) {
                onFormSubmit();
            }
        }
    }
}

const useRegisterFields = (
    kcContext: KcContext_Registration,
    legal: boolean,
    onLegalChanged: (legal: boolean) => void,
    resident: boolean,
    onResidentChanged: (resident: boolean) => void,
    head: boolean,
    onRoleChanged: (head: boolean) => void
): RegisterFields => {
    const { register } = kcContext;
    const { formData } = register;
    const { residency, clientType, legalRole, lastName, firstName, middleName, email, phoneNumber, bin, iin } = formData;
    return {
        residency: useField(isNotEmpty, (value) => { onResidentChanged(value === 'resident') }, residency, extractError(kcContext, "residency")),
        clientType: useField(isNotEmpty, (value) => { onLegalChanged(value === 'legal') }, clientType, extractError(kcContext, "clientType")),
        legalRole: useField(isNotEmptyOnLegal(() => legal), (value) => { onRoleChanged(value === 'head') }, legalRole, extractError(kcContext, "legalRole")),
        lastName: useField(isNotEmpty, () => { }, lastName, extractError(kcContext, "lastName")),
        firstName: useField(isNotEmpty, () => { }, firstName, extractError(kcContext, "firstName")),
        middleName: useField(allAllowed, () => { }, middleName, extractError(kcContext, "middleName")),
        email: useField(isNotEmpty, () => { }, email, extractError(kcContext, "email")),
        bin: useField(isBinIinOnLegalAndResident(() => legal, () => resident, () => head), () => { }, bin, extractError(kcContext, "bin")),
        iin: useField(isBinIinOnResident(() => resident), () => { }, iin, extractError(kcContext, "iin")),
        phoneNumber: useField(isNotEmpty, () => {}, phoneNumber, extractError(kcContext, "phoneNumber")),
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

const isNotEmptyOnLegal = (legal: () => boolean) => (value: string): string | undefined => legal() ? isNotEmpty(value) : undefined;

const isBinIinOnLegalAndResident = (legal: () => boolean, resident: () => boolean, head: () => boolean) => 
    (value: string): string | undefined => legal() ? isValidBinOrCode(resident, head)(value) : undefined;

const isValidBinOrCode = (resident: () => boolean, head: () => boolean) =>
    (value: string): string | undefined => resident() ? isBinIinOnResident(resident)(value) : isValidOrgCode(head)(value);

const isValidOrgCode = (head: () => boolean) => (value: string): string | undefined => head() ? undefined : is12CharsAndStartsWithNR(value);

const is12CharsAndStartsWithNR = (value: string): string | undefined => value.length !== 12 || !value.startsWith("NR") ? "invalidOrgCode" : undefined;

const isBinIinOnResident = (resident: () => boolean) => (value: string): string | undefined => resident() ? isNotEmpty(value) || is12CharsAndNumeric(value) : undefined;

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
