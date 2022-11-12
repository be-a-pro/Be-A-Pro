import { ReactComponent as CheckBox } from '../../../images/icons/checkBox.svg';
import styles from './CheckBox.module.scss';

export default function Checkbox(props) {
    return (
        <div className={styles.checkBox}>
            <CheckBox className={styles.symbol}/>
            <span>{props.text}</span>
        </div>
    )
}