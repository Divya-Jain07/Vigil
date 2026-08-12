import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/common/Layout';
import Dashboard from './pages/Dashboard';
import Analysis from './pages/Analysis';
import History from './pages/History';
import Login from './pages/Login';

function App() {
  // Simple auth check simulation for now
  const isAuthenticated = false;

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="analysis/:id" element={<Analysis />} />
          <Route 
            path="history" 
            element={isAuthenticated ? <History /> : <Navigate to="/login" replace />} 
          />
        </Route>
        <Route path="/login" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;
