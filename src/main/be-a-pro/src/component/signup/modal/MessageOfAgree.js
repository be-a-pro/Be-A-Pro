import ModalFrame from "./ModalFrame";
import styles from './MessageOfAgree.module.scss';
import { ReactComponent as Logo } from '../../../images/be-a-pro-b.svg';
import { ReactComponent as CheckBox } from '../../../images/icons/checkBox.svg';

export default function MessageOfAegree(props) {

    return (
        <ModalFrame state={props.state} setState={props.setState}>
            <Logo className={styles.logoOfSignup} />
            <div className={styles.sectionOfAgree}>
                <div className={styles.articleOfCheckBox}>
                    <CheckBox className={styles.checkBox} />
                    <span className={styles.checkOfAllText}>전체 이용약관 동의</span>
                </div>
                <hr className={styles.splitOfLine} />
                <div className={styles.articleOfCheckBox}>
                    <CheckBox className={styles.checkBox} />
                    <span>[필수] 서비스 이용약관 안내</span>
                </div>
                <div className={styles.articleOfCheckBox}>
                    <CheckBox className={styles.checkBox} />
                    <span>[필수] 개인정보취급방침 안내</span>
                </div>
                <hr className={styles.splitOfLine} />
                <div className={styles.articleOfCheckBox}>
                    <CheckBox className={styles.checkBox} />
                    <span>[선택] 이메일 마케팅 수신동의</span>
                </div>
                <div className={styles.articleOfCheckBox}>
                    <CheckBox className={styles.checkBox} />
                    <span>[선택] SMS 마케팅 수신동의</span>
                </div>
            </div>
            <button className={styles.submitBtn}>동의하고 계속하기</button>
        </ModalFrame>
    )
}