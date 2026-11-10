// Network chunks can split an SSE frame, JSON string, or UTF-8 character anywhere.
export async function consumeSseStream(response, onEvent) {
    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.msg || `请求失败（HTTP ${response.status}）`);
    }
    if (!response.body) throw new Error('浏览器未收到回复流');
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let pending = '', completed = false;
    const parse = (frame) => {
        const data = frame.split(/\r?\n/).filter(line => line.startsWith('data:'))
            .map(line => line.slice(5).replace(/^ /, '')).join('\n');
        if (!data) return;
        if (data === '[DONE]') { completed = true; return; }
        const event = JSON.parse(data);
        if (event.error) throw new Error(event.error);
        onEvent(event);
    };
    try {
        while (true) {
            const { done, value } = await reader.read();
            pending += done ? decoder.decode() : decoder.decode(value, { stream: true });
            const frames = pending.split(/\r?\n\r?\n/);
            pending = frames.pop();
            frames.forEach(parse);
            if (done) break;
        }
        if (pending.trim()) parse(pending);
        if (!completed) throw new Error('回复中断，请重试');
    } finally { reader.releaseLock(); }
}
