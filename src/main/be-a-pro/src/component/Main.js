import logo from '../logo.svg';
import '../App.css';
import React, {useEffect, useState} from 'react';
import axios from 'axios';

function Main() {
    const [hello, setHello] = useState('')

    useEffect(() => {
        axios.get('/api/hello')
        .then(response => setHello(response.data))
        .catch(error => console.log(error))
    }, []);

    return (
        <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p>
            Edit <code>src/App.js</code> and save to reload.
          </p>
          <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            백엔드에서 가져온 데이터입니다 : {hello}
          </a>
        </header>
      </div>    )
}

export default Main