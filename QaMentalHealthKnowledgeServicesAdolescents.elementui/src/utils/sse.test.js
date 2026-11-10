import test from 'node:test';
import assert from 'node:assert/strict';
import { consumeSseStream } from './sse.js';

function response(text, size = 1, status = 200) {
    const bytes = new TextEncoder().encode(text);
    return new Response(new ReadableStream({ start(controller) {
        for (let i = 0; i < bytes.length; i += size) controller.enqueue(bytes.slice(i, i + size));
        controller.close();
    } }), { status });
}
test('preserves Chinese and JSON across byte boundaries and receives saved before done', async () => {
    const events = [];
    await consumeSseStream(response('data: {"type":"content","content":"睡眠与压力"}\r\n\r\ndata: {"type":"saved","messageId":12}\n\ndata: [DONE]\n\n'), e => events.push(e));
    assert.equal(events[0].content, '睡眠与压力'); assert.equal(events[1].messageId, 12);
});
test('does not accept a truncated response as success', async () => {
    await assert.rejects(consumeSseStream(response('data: {"type":"content","content":"partial"}\n\n'), () => {}), /中断/);
});
test('reports provider error without consuming it as an answer', async () => {
    await assert.rejects(consumeSseStream(response('data: {"type":"error","error":"模型余额不足"}\n\n'), () => {}), /余额不足/);
});
test('handles HTTP permission errors', async () => {
    await assert.rejects(consumeSseStream(response('{"msg":"请先登录"}', 3, 401), () => {}), /请先登录/);
});
