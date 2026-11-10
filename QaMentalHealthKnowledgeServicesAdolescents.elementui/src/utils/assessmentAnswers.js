export const parseQuestionOptions = (question) => {
    let options;
    try { options = typeof question.optionJson === 'string' ? JSON.parse(question.optionJson) : question.optionJson; }
    catch { return []; }
    if (!Array.isArray(options)) return [];
    if (!options.every(option => option && typeof option.label === 'string' && option.score !== null && option.score !== '' && Number.isFinite(Number(option.score)))) return [];
    return options.map(option => ({ label: option.label, score: Number(option.score) }));
};

export const hasQuestionAnswer = (questions, answers, index) => Number.isInteger(answers[index]) && !!parseQuestionOptions(questions[index] || {})[answers[index]];
export const answeredQuestionCount = (questions, answers) => questions.reduce((count, _, index) => count + Number(hasQuestionAnswer(questions, answers, index)), 0);

export const evaluateAssessment = (questions, answers, rules) => {
    if (!questions.length || questions.some((_, index) => !hasQuestionAnswer(questions, answers, index))) throw new Error('请完成所有题目后再提交。');
    const answerDetails = questions.map((question, index) => ({ questionIndex: question.questionIndex ?? index + 1, selectedScore: parseQuestionOptions(question)[answers[index]].score }));
    const totalScore = answerDetails.reduce((total, answer) => total + answer.selectedScore, 0);
    const rule = rules.find(item => totalScore >= Number(item.minScore) && totalScore <= Number(item.maxScore));
    if (!rule) throw new Error('评分规则暂不完整，请稍后再试。');
    return { totalScore, resultLabel: rule.resultLabel || '', resultInterpretation: rule.resultInterpretation || '', suggestion: rule.suggestion || '', answerDetails };
};

export const localFinishTime = (date = new Date()) => {
    const pad = value => String(value).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};
