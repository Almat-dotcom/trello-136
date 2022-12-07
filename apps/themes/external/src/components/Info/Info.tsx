import { Layout } from "components/Layout";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_Info = Extract<KcContext, { pageId: "info.ftl" }>;

const Info = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Info, i18n: I18n } & KcProps) => {
    const { requiredActions, messageHeader, message, pageRedirectUri, actionUri, client } = kcContext;
    const { msg, advancedMsgStr } = i18n;

    const requiredActionsItems = requiredActions?.map((it) => <p className="text-gray-700">{advancedMsgStr(it)}</p>) ?? [];

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {messageHeader && (
                        <p className="mt-3 text-slate-900 text-2xl font-bold">{advancedMsgStr(messageHeader) ?? messageHeader}</p>
                    )}

                    {message?.summary && (
                        <p className="mt-3 text-slate-900">{advancedMsgStr(message.summary) ?? message.summary}</p>
                    )}
                </div>

                <div className="mt-4">
                    <>
                        {requiredActionsItems}
                        {(() => {
                            if (pageRedirectUri) {
                                return (
                                    <p className="text-gray-700"><a href={pageRedirectUri} className="font-bold text-blue-700 hover:text-primary hover:underline">{msg("backToApplication")}</a></p>
                                );
                            } else if (actionUri) {
                                return (
                                    <p className="text-gray-700"><a href={actionUri} className="font-bold text-blue-700 hover:text-primary hover:underline">{msg("proceedWithAction")}</a></p>
                                );
                            } else if (client?.baseUrl) {
                                return (
                                    <p className="text-gray-700"><a href={client.baseUrl} className="font-bold text-blue-700 hover:text-primary hover:underline">{msg("backToApplication")}</a></p>
                                );
                            }
                        })()}
                    </>
                </div>
            </div>
        </Layout>
    );
})

export default Info;
