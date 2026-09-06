# OpsBoard

OpsBoard is a React and TypeScript reliability console prototype showing sample services, incidents, and deployments. All metrics and activity are fictional demo data; it does not connect to real production services.

## Highlights

- Responsive dashboard layout with a mobile navigation drawer.
- Search services by name and select any health filter.
- Download the sample service metrics as a CSV report.
- Service table with latency, uptime, request volume, and inline SVG trends.
- Incident and deployment activity panels designed for fast operational scanning.
- Accessible labels, keyboard focus states, semantic table markup, and reduced-motion support.

## Run locally

Requires Node.js 22 or later. Run commands from this project directory.

```bash
npm ci
npm run dev
```

## Stack

React · TypeScript · Vite · Lucide icons · CSS

Run `npm run build` for TypeScript checks and a production bundle. Run `npm run preview` to serve that bundle locally. The data source is the typed `services` collection in `src/App.tsx`; there is no backend, polling, WebSocket stream, authentication, or alert delivery.

The next production step would be replacing the typed mock data with a polling or WebSocket data adapter while keeping the UI components unchanged.
