import type { I18n } from "lib/i18n";

const Footer = ({ i18n }: { i18n: I18n }) => {
    const { msgStr } = i18n;

    return (
        <div className="flex flex-col-reverse md:flex-row items-center md:items-stretch md:justify-between w-full px-1 md:px-8 lg:px-24 xl:px-44 py-8 md:py-12 bg-slate-800">
            <div className="flex flex-col justify-between w-64 md:w-auto">
                <div className="mb-20 text-white text-sm">
                    <div className="inline-block mr-4"><a className="underline" href="/oops">{msgStr("goToSite")}</a></div>
                    <div className="inline-block"><a className="underline" href="/oops">{msgStr("checkDoc")}</a></div>
                </div>
                <div>
                    <span className="text-slate-500 text-sm">{"© " + msgStr("copyright")}</span>
                </div>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-slate-400">{msgStr("contacts")}</div>
                <ul className="text-white">
                    <li className="mb-1">+7 702 702 09 06</li>
                    <li className="mb-1">+7 702 702 22 22</li>
                    <li className="mb-1">info@mail.com</li>
                </ul>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-slate-400">{msgStr("address")}</div>
                <div className="text-white">
                    <p>Казахстан, г. Алматы</p>
                    <p>ул. Орманова, 18</p>
                </div>
            </div>
        </div>
    );
};

export default Footer;
