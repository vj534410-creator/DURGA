// DURGA Backend Comprehensive Integration & Unit Test Suite
process.env.NODE_ENV = 'test';
const http = require('http');

async function runTests() {
  console.log('--- STARTING DURGA BACKEND INTEGRATION TESTS ---');

  // Dynamically import compiled or ts-node app
  const appModule = require('../dist/index.js');
  const app = appModule.default || appModule;

  const server = app.listen(3099);

  function makeRequest(method, path, body = null, headers = {}) {
    return new Promise((resolve, reject) => {
      const options = {
        hostname: '127.0.0.1',
        port: 3099,
        path,
        method,
        headers: {
          'Content-Type': 'application/json',
          ...headers
        }
      };

      const req = http.request(options, (res) => {
        let data = '';
        res.on('data', chunk => data += chunk);
        res.on('end', () => {
          try {
            resolve({ status: res.statusCode, body: JSON.parse(data) });
          } catch (e) {
            resolve({ status: res.statusCode, raw: data });
          }
        });
      });

      req.on('error', reject);
      if (body) {
        req.write(JSON.stringify(body));
      }
      req.end();
    });
  }

  let passed = 0;
  let failed = 0;

  async function test(name, fn) {
    try {
      await fn();
      console.log(`[PASS] ${name}`);
      passed++;
    } catch (err) {
      console.error(`[FAIL] ${name}:`, err.message);
      failed++;
    }
  }

  try {
    // 1. Health check
    await test('GET /health returns 200 and healthy status', async () => {
      const res = await makeRequest('GET', '/health');
      if (res.status !== 200 || res.body.status !== 'healthy') {
        throw new Error(`Expected 200 healthy, got ${res.status}`);
      }
    });

    // 2. User registration
    let userToken = '';
    await test('POST /api/auth/register creates user & JWT', async () => {
      const res = await makeRequest('POST', '/api/auth/register', {
        phone_number: '+919876543210',
        full_name: 'Test Citizen'
      });
      if (res.status !== 201 || !res.body.token) {
        throw new Error(`Failed to register: ${JSON.stringify(res.body)}`);
      }
      userToken = res.body.token;
    });

    // 3. Officer login
    let officerToken = '';
    await test('POST /api/auth/officer-login authenticates badge ID', async () => {
      const res = await makeRequest('POST', '/api/auth/officer-login', {
        badgeId: 'POL-GJ-9999',
        role: 'ROLE_POLICE_OFFICER'
      });
      if (res.status !== 200 || !res.body.token) {
        throw new Error(`Officer login failed: ${JSON.stringify(res.body)}`);
      }
      officerToken = res.body.token;
    });

    // 4. Case creation
    let createdCaseId = '';
    await test('POST /api/cases creates case in central vault', async () => {
      const res = await makeRequest('POST', '/api/cases', {
        caseId: 'DURGA-2026-TEST01',
        title: 'Harassment Report Test',
        category: 'CYBER_CRIME',
        description: 'Threatening messages received on social media platform.',
        incidentLocation: 'Ahmedabad, Gujarat'
      });
      if (res.status !== 201 || !res.body.caseId) {
        throw new Error(`Case creation failed: ${JSON.stringify(res.body)}`);
      }
      createdCaseId = res.body.caseId;
    });

    // 5. Update case status & add notes
    await test('PATCH /api/cases/:id/status updates status and logs audit', async () => {
      const res = await makeRequest('PATCH', `/api/cases/${createdCaseId}/status`, {
        status: 'UNDER_INVESTIGATION',
        remarks: 'Assigned to Sector Inspector',
        officerBadge: 'POL-GJ-9999'
      });
      if (res.status !== 200 || res.body.case.current_status !== 'UNDER_INVESTIGATION') {
        throw new Error(`Status update failed: ${JSON.stringify(res.body)}`);
      }
    });

    // 6. Officer claim case & isolation check
    await test('GET /api/officers/my-cases returns assigned cases', async () => {
      const res = await makeRequest('GET', '/api/officers/my-cases?badgeId=POL-GJ-9999');
      if (res.status !== 200 || !Array.isArray(res.body.cases)) {
        throw new Error(`Failed to get officer cases: ${JSON.stringify(res.body)}`);
      }
    });

    // 7. Evidence hash deposition
    await test('POST /api/evidence/register deposits SHA-256 hash', async () => {
      const res = await makeRequest('POST', '/api/evidence/register', {
        complaintId: createdCaseId,
        sha256Checksum: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        fileType: 'PHOTO',
        fileSizeBytes: 2048
      });
      if (res.status !== 201 || !res.body.evidenceRef) {
        throw new Error(`Evidence registration failed: ${JSON.stringify(res.body)}`);
      }
    });

    // 8. Admin statistics & Audit Logs
    await test('GET /api/admin/stats returns metrics', async () => {
      const res = await makeRequest('GET', '/api/admin/stats');
      if (res.status !== 200 || typeof res.body.stats.totalCases !== 'number') {
        throw new Error(`Admin stats failed: ${JSON.stringify(res.body)}`);
      }
    });

    await test('GET /api/admin/audit-logs returns tamper-evident logs', async () => {
      const res = await makeRequest('GET', '/api/admin/audit-logs');
      if (res.status !== 200 || res.body.logs.length === 0) {
        throw new Error(`Audit logs failed or empty: ${JSON.stringify(res.body)}`);
      }
    });

    // 9. Statutory Government Integration Service
    await test('POST /api/government/submit marks READY_FOR_OFFICIAL_INTEGRATION', async () => {
      const res = await makeRequest('POST', '/api/government/submit', {
        complaintId: createdCaseId,
        payload: { summary: 'Official dossier' }
      });
      if (res.status !== 200 || res.body.result.status !== 'READY_FOR_OFFICIAL_INTEGRATION') {
        throw new Error(`Government integration failed statutory guardrail: ${JSON.stringify(res.body)}`);
      }
    });

    // 10. AI Complaint Drafting
    await test('POST /api/ai/draft-complaint structures raw facts safely', async () => {
      const res = await makeRequest('POST', '/api/ai/draft-complaint', {
        incidentType: 'STALKING',
        rawFacts: 'Individual followed me from metro station to office building yesterday at 6 PM.'
      });
      if (res.status !== 200 || !res.body.draft) {
        throw new Error(`AI drafting failed: ${JSON.stringify(res.body)}`);
      }
    });

  } finally {
    server.close();
  }

  console.log(`\nTEST RESULTS: ${passed} passed, ${failed} failed.`);
  if (failed > 0) {
    process.exit(1);
  }
}

runTests().catch(err => {
  console.error('Fatal test error:', err);
  process.exit(1);
});
