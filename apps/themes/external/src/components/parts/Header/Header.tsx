import { I18n } from "lib/i18n";
import Lang from "../Lang";
import { ReactComponent as Logo } from './logo.svg';
import FAQ from "./faq.svg";
import Manuals from "./manuals.svg";
import Download from "./download.svg";
import { KcContext } from "lib/kc";

const Header = ({ kcContext, current, langs, i18n }: { kcContext: KcContext, current: string, langs: any[], i18n: I18n }) => {
    const { msgStr } = i18n;
    return (
        <>
            <div className="fixed flex items-center justify-between z-50 w-full bg-white border-b-2 border-gray-200">
                <div className="my-6 pl-4 xl:pl-52">
                    <a href="https://cabinet.kacd.kz"><Logo /></a>
                </div>
                <div className="flex items-center pr-4 xl:pr-52">
                    <a className="flex items-center mr-4 text-dark-text hover:text-primary-focus" href="https://cabinet.kacd.kz/faq">
                        <img className="inline-block mr-2" alt="faq" src={FAQ}/><span className="hidden md:inline-block">{msgStr("faq")}</span>
                    </a>
                    <a className="flex items-center mr-4 text-dark-text hover:text-primary-focus" href={kcContext.url.resourcesPath + "/build/instLKRegistration.pdf"} download>
                        <img className="inline-block mr-2" alt="faq" src={Manuals}/>
                        <span className="hidden lg:inline-block mr-2">{msgStr("manuals")}</span>
                        <img className="hidden lg:inline-block" alt="faq" src={Download}/>
                    </a>
                    <Lang current={current} langs={langs} i18n={i18n} />
                </div>
            </div>
        </>
    );
}

export default Header;
