import styles from './PositionBtn.module.scss';
import { ReactComponent as RemoveTag } from '../../../images/icons/removeTag.svg';

export default function PostionBtn(props) {
    return (
        <div className={styles.btn}>
            <span className={styles.text}>{props.text}</span>
            <RemoveTag className={styles.removeTag}/>
        </div>
    )
}