import { parseQuestionOptions } from './assessmentAnswers.js';

export const parseRecordedAnswers = (value) => {
    if (!value) return { answers: [], invalid: false };
    try {
        const answers = typeof value === 'string' ? JSON.parse(value) : value;
        return Array.isArray(answers) ? { answers: answers.map(item => item && typeof item === 'object' ? item : null), invalid: false } : { answers: [], invalid: true };
    } catch { return { answers: [], invalid: true }; }
};

const hasIndex = (answer) => !!answer && answer.questionIndex !== undefined && answer.questionIndex !== null && answer.questionIndex !== '';
const recordedScore = (answer) => answer?.selectedScore !== undefined && answer.selectedScore !== null && answer.selectedScore !== '' && Number.isFinite(Number(answer.selectedScore)) ? Number(answer.selectedScore) : null;

export const buildAssessmentReview = (questions, value) => {
    const { answers, invalid } = parseRecordedAnswers(value);
    const indexed = answers.some(hasIndex);
    const ordered = [...questions].sort((a, b) => (Number(a.questionIndex) || 0) - (Number(b.questionIndex) || 0));
    return {
        invalid,
        rows: ordered.map((question, position) => {
            const options = parseQuestionOptions(question);
            const questionIndex = question.questionIndex ?? position + 1;
            const matches = indexed ? answers.filter(answer => hasIndex(answer) && Number(answer.questionIndex) === Number(questionIndex)) : answers[position] ? [answers[position]] : [];
            const answer = matches.length === 1 ? matches[0] : null;
            const score = recordedScore(answer);
            const matchingOptions = score === null ? [] : options.filter(option => option.score === score);
            let label = '', status = 'missing';
            if (matches.length > 1) { label = '历史记录重复，暂无法确认选择'; status = 'ambiguous'; }
            else if (answer && typeof answer.selectedLabel === 'string' && answer.selectedLabel.trim()) { label = answer.selectedLabel; status = 'recorded'; }
            else if (matchingOptions.length === 1) { label = matchingOptions[0].label; status = 'recorded'; }
            else if (score !== null) { label = `记录得分：${score} 分（选项无法唯一对应）`; status = 'ambiguous'; }
            else label = '暂无该题的作答记录';
            return { id: question.id ?? questionIndex, questionIndex, questionTitle: question.questionTitle || `第 ${questionIndex} 题`, options, selectedScore: score, selectedLabel: label, status };
        })
    };
};
