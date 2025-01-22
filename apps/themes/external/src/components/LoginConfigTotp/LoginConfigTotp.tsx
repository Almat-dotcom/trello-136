import { Layout } from "components/Layout";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import React, { memo } from "react";

type KcContext_LoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const LoginConfigTotp = memo((
  {
    kcContext,
    i18n,
    ...props
  }: {
    kcContext: KcContext_LoginConfigTotp;
    i18n: I18n;
  } & KcProps
) => {

  const { url, totp, mode } = kcContext;

  // Допустим, у нас есть объект fields, где мы можем хранить состояние поля OTP:
  const fields = {
    otp: {
      value: "",                      // Текущее значение
      error: "",                      // Ошибка, если есть
      onChange: (value: string) => {  // Обновление значения
        fields.otp.value = value;
      },
    },
  };

  // Для перевода, допустим, у нас есть функция msgStr:
  const { msgStr } = i18n;

  return (
    <Layout kcContext={kcContext} i18n={i18n}>
      <h1>{msgStr("setupTotp") /* "Настройка TOTP" */}</h1>

      {mode === "qr" && (
        <>
          <p>{msgStr("scanQrCode") /* "Отсканируйте QR-код..." */}</p>
          <img
            src={totp.qrUrl}
            alt="TOTP QR Code"
            width="150"
            height="150"
            style={{ border: "1px solid #ccc" }}
          />
        </>
      )}

      <p>{msgStr("enterKeyManually") /* "Или введите ключ вручную" */}</p>
      <pre>{totp.totpSecret}</pre>

      <form action={url.loginAction} method="POST" style={{ marginTop: "1rem" }}>
        <InputField
          fieldName="otp"
          label={msgStr("enterOtp")} // Например, "Введите одноразовый код"
          type="text"
          value={fields.otp.value}
          required
          // error — это сообщение об ошибке, если нужно.
          // Если поле валидно, можно передавать пустую строку или undefined.
          error={fields.otp.error}
          onChange={(event) => fields.otp.onChange(event.target.value)}
        />

        <Button severity="primary" type="submit">
          {msgStr("confirm")}
        </Button>
      </form>
    </Layout>
  );
});

export default LoginConfigTotp;