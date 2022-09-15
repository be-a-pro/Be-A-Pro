import logo from './logo.svg';
import './App.css';
import React, {useEffect, useState} from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import axios from 'axios';
import Main from './component/Main';
import ProjectWrite from './component/project/ProejctWrite';
import ProjectList from './component/project/ProjectList';

function App() {

  return (

    <BrowserRouter>
    <div class="App">
      <Routes>
        <Route path='/' element={<Main/>}></Route> , {/* 메인화면 라우터 */}
        <Route path='/project' element={<ProjectList/>}></Route> , {/* 프로젝트 리스트 라우터 */}
        <Route path='/projectWrite' element={<ProjectWrite/>}></Route> , {/* 프로젝트 라우터 */}
      </Routes>
    </div>
    </BrowserRouter>
  );
}

export default App;
