<template>
    <div class="s-canvas" @click="refreshCode">
        <canvas id="s-canvas" :width="contentWidth" :height="contentHeight"></canvas>
    </div>
</template>

<script>
import { defineComponent, onMounted, ref } from 'vue';

export default defineComponent({
    name: 'ImageCode',
    props: {
        fontSizeMin: {
            type: Number,
            default: 30
        },
        fontSizeMax: {
            type: Number,
            default: 40
        },
        backgroundColorMin: {
            type: Number,
            default: 180
        },
        backgroundColorMax: {
            type: Number,
            default: 240
        },
        colorMin: {
            type: Number,
            default: 50
        },
        colorMax: {
            type: Number,
            default: 160
        },
        lineColorMin: {
            type: Number,
            default: 40
        },
        lineColorMax: {
            type: Number,
            default: 180
        },
        dotColorMin: {
            type: Number,
            default: 0
        },
        dotColorMax: {
            type: Number,
            default: 255
        },
        contentWidth: {
            type: Number,
            default: 180
        },
        contentHeight: {
            type: Number,
            default: 40
        },
    },
    setup(props) {
        const identifyCode = ref("");
        const identifyCodes = '1234567890';

        // 生成一个随机数
        const randomNum = (min, max) => {
            return Math.floor(Math.random() * (max - min) + min)
        }

        // 生成一个随机的颜色
        const randomColor = (min, max) => {
            let r = randomNum(min, max)
            let g = randomNum(min, max)
            let b = randomNum(min, max)
            return 'rgb(' + r + ',' + g + ',' + b + ')'
        }

        const drawText = (ctx, txt, i) => {
            ctx.fillStyle = randomColor(props.colorMin, props.colorMax)
            ctx.font = randomNum(props.fontSizeMin, props.fontSizeMax) + 'px SimHei'
            let x = (i + 1) * (props.contentWidth / (identifyCode.value.length + 1))
            let y = randomNum(props.fontSizeMax, props.contentHeight - 5)
            var deg = randomNum(-45, 45)
            // 修改坐标原点和旋转角度
            ctx.translate(x, y)
            ctx.rotate(deg * Math.PI / 180)
            ctx.fillText(txt, 0, 0)
            // 恢复坐标原点和旋转角度
            ctx.rotate(-deg * Math.PI / 180)
            ctx.translate(-x, -y)
        }

        const drawLine = (ctx) => {
            // 绘制干扰线
            for (let i = 0; i < 8; i++) {
                ctx.strokeStyle = randomColor(props.lineColorMin, props.lineColorMax)
                ctx.beginPath()
                ctx.moveTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
                ctx.lineTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
                ctx.stroke()
            }
        }

        const drawDot = (ctx) => {
            // 绘制干扰点
            for (let i = 0; i < 100; i++) {
                ctx.fillStyle = randomColor(0, 255)
                ctx.beginPath()
                ctx.arc(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight), 1, 0, 2 * Math.PI)
                ctx.fill()
            }
        }

        const makeCode = (o, l) => {
            identifyCode.value = '';
            for (let i = 0; i < l; i++) {
                identifyCode.value += identifyCodes[
                    randomNum(0, identifyCodes.length)]
            }
        }

        const drawPic = () => {
            let canvas = document.getElementById('s-canvas')
            let ctx = canvas.getContext('2d')
            ctx.textBaseline = 'bottom'
            // 绘制背景
            ctx.fillStyle = randomColor(props.backgroundColorMin, props.backgroundColorMax)
            ctx.fillRect(0, 0, props.contentWidth, props.contentHeight)
            // 绘制文字
            for (let i = 0; i < identifyCode.value.length; i++) {
                drawText(ctx, identifyCode.value[i], i)
            }
            drawLine(ctx)
            drawDot(ctx)
        }

        // 切换验证码
        const refreshCode = () => {
            identifyCode.value = ''
            makeCode(identifyCodes, 4)
            drawPic()
        }

        const getCode = () => {
            return identifyCode.value;
        }

        onMounted(() => {
            makeCode(identifyCodes, 4)
            drawPic()
        })

        return {
            identifyCode,
            drawPic,
            refreshCode,
            getCode,
            contentWidth: props.contentWidth,
            contentHeight: props.contentHeight
        }
    }
})
</script>

<style scoped>
/* 验证码容器 */
.s-canvas {
    cursor: pointer;
}
</style>
