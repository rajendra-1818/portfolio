import { useMemo, useState } from 'react';
import { Activity, AlertTriangle, ArrowUpRight, CheckCircle2, Clock3, Menu, Search, ShieldCheck, X } from 'lucide-react';

type Health = 'Healthy' | 'Degraded' | 'Investigating';
type Service = { name: string; owner: string; health: Health; latency: number; uptime: number; requests: string; trend: number[]; };

const services: Service[] = [
  { name: 'checkout-api', owner: 'Commerce', health: 'Healthy', latency: 182, uptime: 99.99, requests: '2.4M', trend: [5, 7, 6, 8, 7, 9, 8, 10, 9, 11] },
  { name: 'identity-gateway', owner: 'Platform', health: 'Healthy', latency: 96, uptime: 99.98, requests: '1.8M', trend: [10, 9, 11, 10, 12, 11, 13, 12, 14, 13] },
  { name: 'notification-worker', owner: 'Engagement', health: 'Degraded', latency: 641, uptime: 99.72, requests: '862K', trend: [6, 8, 7, 12, 15, 13, 18, 17, 21, 19] },
  { name: 'analytics-stream', owner: 'Data', health: 'Investigating', latency: 388, uptime: 98.94, requests: '4.1M', trend: [12, 11, 13, 15, 14, 18, 17, 20, 23, 22] },
];

function Sparkline({ points, danger = false }: { points: number[]; danger?: boolean }) {
  const max = Math.max(...points), min = Math.min(...points);
  const path = points.map((p, i) => `${(i / (points.length - 1)) * 100},${100 - ((p - min) / (max - min || 1)) * 75 - 12}`).join(' ');
  return <svg className="spark" viewBox="0 0 100 100" preserveAspectRatio="none" aria-label="Trend chart"><polyline points={path} fill="none" stroke={danger ? '#f08c72' : '#91a5ff'} strokeWidth="3" vectorEffect="non-scaling-stroke" /></svg>;
}

function downloadReport() {
  const rows = [['Service', 'Owner', 'Health', 'Latency (ms)', 'Uptime (%)', 'Requests'],
    ...services.map(s => [s.name, s.owner, s.health, s.latency, s.uptime, s.requests])];
  const csv = rows.map(row => row.map(value => '"' + String(value).replaceAll('"', '""') + '"').join(',')).join('\n');
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8;' }));
  const link = document.createElement('a');
  link.href = url;
  link.download = 'opsboard-demo-services.csv';
  document.body.append(link);
  link.click();
  link.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function App() {
  const [filter, setFilter] = useState<Health | 'All'>('All');
  const [query, setQuery] = useState('');
  const [open, setOpen] = useState(false);
  const filtered = useMemo(() => services.filter(service => (filter === 'All' || service.health === filter) && service.name.includes(query.trim().toLowerCase())), [filter, query]);
  const degraded = services.filter(s => s.health !== 'Healthy').length;
  return <div className="app">
    <aside className={open ? 'sidebar open' : 'sidebar'}><div className="brand"><div className="brand-mark">O</div><span>opsboard</span><button className="close" onClick={() => setOpen(false)} aria-label="Close navigation"><X size={18}/></button></div><div className="workspace"><span className="avatar">RD</span><span><strong>Sri’s workspace</strong><small>Production</small></span></div><nav><p className="nav-label">MONITORING</p><a className="active" href="#overview"><Activity size={17}/>Overview</a><a href="#services"><ShieldCheck size={17}/>Services<span className="nav-count">{services.length}</span></a><a href="#incidents"><AlertTriangle size={17}/>Incidents<span className="nav-count alert">2</span></a><a href="#deployments"><ArrowUpRight size={17}/>Deployments</a></nav><div className="sidebar-bottom"><div className="online-dot"/>Demo data<small>No live services connected</small></div></aside>
    <main><header className="topbar"><button className="menu" onClick={() => setOpen(true)} aria-label="Open navigation"><Menu size={20}/></button><div className="breadcrumb">Overview <span>/</span> Production</div><div className="top-actions"><div className="search"><Search size={16}/><input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search services" aria-label="Search services"/></div><a className="icon-button" href="#incidents" aria-label="View incidents"><AlertTriangle size={17}/><i/></a><span className="top-avatar">RD</span></div></header>
      <div className="content" id="overview"><div className="title-row"><div><p className="kicker">RELIABILITY CONSOLE · DEMO DATA</p><h1>Good morning, Sri.</h1><p className="subhead">Explore a sample production environment.</p></div><button className="primary-button" onClick={downloadReport}>Download service report <ArrowUpRight size={16}/></button></div>
        <section className="metric-grid"><div className="metric-card"><div className="metric-head"><span>System health</span><CheckCircle2 size={17} className="green"/></div><strong className="metric-value">99.97%</strong><span className="metric-foot green-text">+0.04% <em>vs last week</em></span></div><div className="metric-card"><div className="metric-head"><span>Open incidents</span><AlertTriangle size={17} className="orange"/></div><strong className="metric-value">{degraded}</strong><span className="metric-foot orange-text">Needs attention <em>across 2 services</em></span></div><div className="metric-card"><div className="metric-head"><span>Requests today</span><Activity size={17} className="blue"/></div><strong className="metric-value">9.2M</strong><span className="metric-foot blue-text">+8.6% <em>vs average</em></span></div><div className="metric-card"><div className="metric-head"><span>Avg. latency</span><Clock3 size={17} className="purple"/></div><strong className="metric-value">247<span className="unit">ms</span></strong><span className="metric-foot purple-text">-12ms <em>vs last week</em></span></div></section>
        <section className="panel" id="services"><div className="panel-header"><div><h2>Service health</h2><p>Sample service metrics · search and filter below</p></div><div className="filter-wrap"><select className="filter-button" value={filter} onChange={e => setFilter(e.target.value as Health | 'All')} aria-label="Filter by health">{['All', 'Healthy', 'Degraded', 'Investigating'].map(value => <option key={value} value={value}>{value}</option>)}</select></div></div><div className="table-wrap"><table><thead><tr><th>Service</th><th>Owner</th><th>Status</th><th>Latency</th><th>Uptime (30d)</th><th>Requests</th><th>Trend</th></tr></thead><tbody>{filtered.map(service => <tr key={service.name}><td><span className="service-icon">{service.name.slice(0, 1).toUpperCase()}</span><strong>{service.name}</strong></td><td className="muted">{service.owner}</td><td><span className={`status ${service.health.toLowerCase()}`}><i/>{service.health}</span></td><td className={service.latency > 400 ? 'bad-value' : ''}>{service.latency} ms</td><td>{service.uptime.toFixed(2)}%</td><td>{service.requests}</td><td className="trend"><Sparkline points={service.trend} danger={service.health !== 'Healthy'}/></td></tr>)}</tbody></table>{filtered.length === 0 && <div className="empty">No services match your filters.</div>}</div></section>
        <div className="lower-grid"><section className="panel incidents" id="incidents"><div className="panel-header"><div><h2>Recent incidents</h2><p>Last 24 hours</p></div></div><div className="incident-list"><div className="incident"><span className="incident-icon orange-bg"><AlertTriangle size={16}/></span><div><strong>Notification delays above SLO</strong><p>notification-worker · 18 minutes ago</p></div><span className="status investigating">Investigating</span></div><div className="incident"><span className="incident-icon red-bg"><Activity size={16}/></span><div><strong>Analytics consumer lag</strong><p>analytics-stream · 2 hours ago</p></div><span className="status degraded">Degraded</span></div><div className="incident resolved"><span className="incident-icon green-bg"><CheckCircle2 size={16}/></span><div><strong>Checkout error spike</strong><p>checkout-api · Resolved 5 hours ago</p></div><span className="status healthy">Resolved</span></div></div></section><section className="panel activity-panel" id="deployments"><div className="panel-header"><div><h2>Deployment activity</h2><p>Latest changes to production</p></div></div><div className="deploy-list"><div className="deploy"><span className="deploy-badge">↗</span><div><strong>checkout-api <small>v2.18.0</small></strong><p>Deployed by Sri Dabburi</p></div><time>32 min ago</time></div><div className="deploy"><span className="deploy-badge">↗</span><div><strong>identity-gateway <small>v1.9.4</small></strong><p>Deployed by Maya Chen</p></div><time>2 hr ago</time></div><div className="deploy"><span className="deploy-badge">↗</span><div><strong>opsboard <small>v0.7.2</small></strong><p>Deployed by Sri Dabburi</p></div><time>Yesterday</time></div></div></section></div>
      </div><footer><span>OpsBoard</span><span>Reliability console prototype · Built with React + TypeScript</span><span>v1.0.0</span></footer>
    </main>
  </div>;
}
export default App;
