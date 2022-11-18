import styles from './KeywordBtn.module.css';
import { ReactComponent as RemoveTag } from '../../../images/icons/removeTag.svg';

export default function KeywordBtn(props) {
    return (
        <div className={styles.btn}>
            <span className={styles.text}>{props.text}</span>
            <RemoveTag className={styles.removeTag} onClick={props.onClick}/>
        </div>
    )
}