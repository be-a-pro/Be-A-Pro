
import styles from './ModalFrame.module.css';
import { ReactComponent as Quit} from '../../images/icons/quit.svg';

export default function ModalFrame({children, state, setState}) {

    function quitSignUp() {
        if (state === true)
        setState(false);
    }

    return (
        <div className={styles.centerLocation}>
            <div className={styles.containerOfSignup}>
                    <Quit className={styles.iconOfQuit} onClick={quitSignUp}/>
                    <div className={styles.contentOfSignup}>
                        <div className={styles.content}>
                            {children}
                        </div>
                    </div>
            </div>
        </div>
    )
}