import type { I18n } from "lib/i18n";
import Network from "./network.svg";
import Document from "./document.svg";
import Phone from './phone.svg';
import WhatsApp from './whatsapp.svg';
import Mail from './mail.svg';

const Footer = ({ i18n }: { i18n: I18n }) => {
    const { msgStr } = i18n;

    return (
        <div className="flex flex-col-reverse md:flex-row items-center md:items-stretch md:justify-between w-full px-1 md:px-8 lg:px-24 xl:px-44 py-8 md:py-12 bg-secondary-background">
            <div className="flex flex-col justify-between md:w-auto">
                <div className="text-sm">
                    <a className="flex justify-center my-1 py-1.5 border border-dark-text hover:border-primary-focus rounded-sm text-center text-dark-text hover:text-primary-focus" href="/oops">
                        <img className="inline-block mr-2" alt="net" src={Network}/>{msgStr("goToSite")}
                    </a>
                    <a className="flex justify-center my-1 py-1.5 border border-dark-text hover:border-primary-focus rounded-sm text-center text-dark-text hover:text-primary-focus" href="/oops">
                        <img className="inline-block mr-2" alt="net" src={Document}/>{msgStr("checkDoc")}
                    </a>
                </div>
                <div>
                    <span className="text-gray-500 text-sm">{"© " + msgStr("copyright")}</span>
                </div>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-dark-text">{msgStr("contacts")}</div>
                <ul className="text-dark-text">
                    <li className="flex items-center justify-start mb-1"><img className="inline-block mr-2" alt="net" src={Phone}/>+7 702 702 09 06</li>
                    <li className="flex items-center justify-start mb-1"><img className="inline-block mr-2" alt="net" src={WhatsApp}/>+7 702 702 22 22</li>
                    <li className="flex items-center justify-start mb-1"><img className="inline-block mr-2" alt="net" src={Mail}/>info@mail.com</li>
                </ul>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-dark-text">{msgStr("address")}</div>
                <div className="text-dark-text">
                    <p>Казахстан, г. Алматы</p>
                    <p>ул. Орманова, 18</p>
                </div>
            </div>
        </div>
    );
};

export default Footer;
