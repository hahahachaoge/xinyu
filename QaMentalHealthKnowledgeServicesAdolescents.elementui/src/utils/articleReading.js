import writingImage from '@/assets/article-detail-writing.png';
import studyImage from '@/assets/chat-calm-corner.png';
import friendsImage from '@/assets/chat-garden-hero.png';
import familyImage from '@/assets/article-family-conversation.png';
import growthImage from '@/assets/article-self-growth.png';

export const articleIllustration = (article) => {
    if (article.coverImage?.trim()) return article.coverImage;
    const topic = article.articleTypeDto?.typeName || '';
    if (/家庭|亲子/.test(topic)) return familyImage;
    if (/学业|学习|压力/.test(topic)) return studyImage;
    if (/人际|社交|交往/.test(topic)) return friendsImage;
    if (/自我|成长|认知/.test(topic)) return growthImage;
    return writingImage;
};

const parseContent = (html) => {
    const document = new DOMParser().parseFromString(html || '', 'text/html');
    document.querySelectorAll('script, style, iframe, object, embed, form, input, button, svg, math, link, meta, base').forEach(node => node.remove());
    document.body.querySelectorAll('*').forEach(node => {
        [...node.attributes].forEach(attribute => {
            const name = attribute.name.toLowerCase();
            if (name.startsWith('on') || ['srcdoc', 'id', 'name', 'class', 'style'].includes(name)) node.removeAttribute(attribute.name);
            if (['src', 'href', 'poster', 'xlink:href', 'action'].includes(name) && !/^(https?:|\/|#|\.\.?\/|data:image\/(png|jpeg|webp|gif);base64,)/i.test(attribute.value.trim())) node.removeAttribute(attribute.name);
            if (name === 'srcset') node.removeAttribute(attribute.name);
        });
        if (node.tagName === 'A') { node.setAttribute('rel', 'noopener noreferrer'); node.setAttribute('target', '_blank'); }
        if (node.tagName === 'IMG') node.setAttribute('loading', 'lazy');
    });
    return document;
};

export const articleExcerpt = (article, length = 90) => {
    const document = parseContent(article.summary || article.articleContent || '');
    return (document.body.textContent || '').replace(/\s+/g, ' ').trim().slice(0, length);
};

const paragraphTitle = (text, index) => {
    if (/给情绪命名|给感受命名|什么情绪/.test(text)) return '给感受一个名字';
    if (/深呼吸|写日记|情绪调节/.test(text)) return '找到适合自己的调节方式';
    if (index === 0 && /识别自己的情绪|情绪是/.test(text)) return '先看见自己的情绪';
    return (text.split(/[。！？\n]/).find(part => part.trim()) || '继续了解').trim().slice(0, 20);
};

export const buildArticleReading = (article) => {
    const document = parseContent(article.articleContent);
    const sourceText = (document.body.textContent || '').replace(/\s+/g, ' ').trim();
    const guide = sourceText.match(/^.*?[。！？]/)?.[0] || sourceText.slice(0, 90);
    let headings = [...document.body.querySelectorAll('h2, h3')];
    if (!headings.length) {
        const contentNodes = [...document.body.childNodes].filter(node => node.nodeType === 1 || node.textContent.trim());
        document.body.replaceChildren();
        let sectionIndex = 0;
        contentNodes.forEach(node => {
            const text = node.textContent.trim();
            if (!text) { document.body.append(node); return; }
            const heading = document.createElement('h2');
            heading.textContent = paragraphTitle(text, sectionIndex++);
            document.body.append(heading, node);
        });
        headings = [...document.body.querySelectorAll('h2')];
    }
    const toc = headings.map((heading, index) => {
        const title = heading.textContent.trim();
        const id = `article-${article.id || 0}-section-${index + 1}`;
        const number = String(index + 1).padStart(2, '0');
        heading.id = id;
        heading.setAttribute('data-reading-heading', '');
        const prefix = document.createElement('span');
        prefix.className = 'section-index';
        prefix.textContent = number;
        heading.prepend(prefix);
        return { id, title, number };
    });
    return { html: document.body.innerHTML, toc, guide };
};
