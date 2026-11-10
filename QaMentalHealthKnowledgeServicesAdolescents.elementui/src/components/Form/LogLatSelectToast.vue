<template>
    <!-- 添加弹窗包装 -->
    <el-dialog title="选择位置" v-model="visible" :close-on-click-modal="false" width="80%" class="location-select-dialog"
        @close="handleClose">
        <div class="address-select">
            <!-- 左侧搜索面板 -->
            <div class="search-panel">
                <div class="panel-header">
                    <h2>地址选择</h2>
                    <p class="subtitle">请搜索或在地图上选择位置</p>
                </div>

                <!-- 搜索框 -->
                <div class="search-box">
                    <el-input v-model="searchForm.address" placeholder="搜索地址..." :prefix-icon="Search" clearable
                        @keyup.enter="searchByKeyword">
                        <template #append>
                            <el-button @click="searchByKeyword">搜索</el-button>
                        </template>
                    </el-input>
                </div>

                <!-- 搜索结果列表 -->
                <div class="search-results" v-if="addressList.length">
                    <div v-for="(item, index) in addressList" :key="index" class="result-item"
                        :class="{ active: isSelected(item) }" @click="selectSearchResult(item)">
                        <div class="item-content">
                            <el-icon>
                                <Location />
                            </el-icon>
                            <div class="item-details">
                                <div class="item-name">{{ item.title }}</div>
                                <div class="item-address">{{ item.address }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 当前选中的地址信息 -->
                <div class="selected-address" v-if="selectAddress.lng">
                    <div class="info-card">
                        <div class="card-header">
                            <el-icon class="success">
                                <Location />
                            </el-icon>
                            <span>已选位置</span>
                        </div>
                        <div class="card-content">

                            <div class="address-detail">
                                <p>详细地址：{{ selectAddress.fullAddress }}</p>
                                <p class="address-components">
                                    <span>省份：{{ selectAddress.province }}</span>
                                    <span>城市：{{ selectAddress.city }}</span>
                                    <span>区县：{{ selectAddress.district }}</span>
                                    <span v-if="selectAddress.street">街道：{{ selectAddress.street }}</span>
                                    <span v-if="selectAddress.streetNumber">门牌号：{{ selectAddress.streetNumber }}</span>
                                </p>
                            </div>
                            <div class="coordinates">
                                经度: {{ selectAddress.lng }}<br>
                                纬度: {{ selectAddress.lat }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 右侧地图 -->
            <div class="map-container">
                <el-skeleton v-if="map == null" :rows="12" animated />
                <div id="containerMap"></div>
            </div>
        </div>
        <!-- 添加底部按钮 -->
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="Save">确 定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
import { Location, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 获取store和router实例
const commonStore = useCommonStore()
const router = useRouter()
const route = useRoute()

// 响应式数据
const searchForm = reactive({ address: "" });
const addressList = ref([]);
const map = ref(null);
const search = ref(null);
const markers = ref(null);
const currentMarker = ref(null);
const currentInfoWindow = ref(null);
const selectAddress = reactive({
    lat: "",
    lng: "",
    name: "",
    province: "",
    city: "",
    district: "",
    street: "",
    streetNumber: "",
    fullAddress: ""
});
const visible = ref(false);
const callback = ref(null);
const scriptsLoaded = ref(false);

// 动态加载地图相关脚本
const loadMapScripts = () => {
    return new Promise((resolve, reject) => {
        // 如果已经加载过，直接返回
        if (window.TMap) {
            scriptsLoaded.value = true;
            resolve();
            return;
        }

        // 记录需要加载的脚本数量
        let loadedCount = 0;
        const totalScripts = 2;

        // 创建和加载第一个脚本
        const script1 = document.createElement('script');
        script1.charset = 'utf-8';
        script1.src = 'https://map.qq.com/api/js?v=2.exp&key=R33BZ-DISK4-4J7UB-D3JHM-GQA3E-V6BBF&libraries=place';
        script1.async = true;

        // 创建和加载第二个脚本
        const script2 = document.createElement('script');
        script2.src = 'https://map.qq.com/api/gljs?v=1.exp&key=R33BZ-DISK4-4J7UB-D3JHM-GQA3E-V6BBF&libraries=service';
        script2.async = true;

        // 脚本加载成功的处理函数
        const handleScriptLoad = () => {
            loadedCount++;
            if (loadedCount === totalScripts) {
                scriptsLoaded.value = true;
                resolve();
            }
        };

        // 脚本加载失败的处理函数
        const handleScriptError = (error) => {
            reject(error);
            ElMessage.error('地图脚本加载失败，请刷新重试');
        };

        // 设置事件监听
        script1.addEventListener('load', handleScriptLoad);
        script1.addEventListener('error', handleScriptError);
        script2.addEventListener('load', handleScriptLoad);
        script2.addEventListener('error', handleScriptError);

        // 添加到文档中
        document.head.appendChild(script1);
        document.head.appendChild(script2);
    });
};

// 方法
const OpenToast = async (Latitude, Longitude, Address, cb) => {
    selectAddress.lat = Latitude;
    selectAddress.lng = Longitude;
    selectAddress.name = Address;
    visible.value = true;
    callback.value = cb;

    // 等待脚本加载完成
    if (!scriptsLoaded.value) {
        try {
            await loadMapScripts();
        } catch (error) {
            console.error('加载地图脚本失败:', error);
            return;
        }
    }

    nextTick(() => {
        InitMap();
    });
};

const InitMap = () => {
    // 确保TMap已经加载
    if (!window.TMap) {
        ElMessage.error('地图组件未加载完成，请稍后再试');
        return;
    }

    const center = selectAddress.lat && selectAddress.lng
        ? new TMap.LatLng(selectAddress.lat, selectAddress.lng)
        : new TMap.LatLng(42.317556, 118.944677);

    map.value = new TMap.Map('containerMap', {
        zoom: selectAddress.lat ? 15 : 6,
        center: center,
        showControl: true,
        mapStyleId: 'style1' // 可以选择适合的地图样式
    });

    // 初始化搜索服务
    search.value = new TMap.service.Search({ pageSize: 10 });

    // 初始化标记图层
    markers.value = new TMap.MultiMarker({
        map: map.value,
        geometries: []
    });

    initializeMarker(center);
    bindMapEvents();
};

const initializeMarker = (position) => {
    if (selectAddress.lat && selectAddress.lng) {
        currentMarker.value = new TMap.MultiMarker({
            map: map.value,
            geometries: [{
                id: 'selected',
                position: position,
            }]
        });

        // 通过逆地理编码重新获取完整地址信息（包括省市区街道等）
        updateSelection({
            lat: selectAddress.lat,
            lng: selectAddress.lng,
            name: selectAddress.name
        });
    }
};

const showInfoWindow = (position, content) => {
    if (currentInfoWindow.value) {
        currentInfoWindow.value.close();
    }

    currentInfoWindow.value = new TMap.InfoWindow({
        map: map.value,
        position: position,
        content: `
            <div class="custom-info-window">
                <h3>${content || '已选位置'}</h3>
                <p>${position.lat.toFixed(6)}, ${position.lng.toFixed(6)}</p>
            </div>
        `,
        offset: { x: 0, y: -50 }
    });
    currentInfoWindow.value.open();
};

const bindMapEvents = () => {
    map.value.on("click", (evt) => {
        updateSelection({
            lat: evt.latLng.lat,
            lng: evt.latLng.lng,
            name: evt.poi ? evt.poi.name : '未知地点'
        });
    });
};

const updateSelection = async (location) => {
    // 创建地址解析器实例
    const geocoder = new TMap.service.Geocoder();

    try {
        // 解析坐标获取详细地址
        const result = await geocoder.getAddress({
            location: new TMap.LatLng(location.lat, location.lng)
        });

        if (result.status === 0 && result.result) {
            const addressInfo = result.result.address_component;
            Object.assign(selectAddress, {
                lat: location.lat,
                lng: location.lng,
                name: location.name,
                province: addressInfo.province || '',
                city: addressInfo.city || '',
                district: addressInfo.district || '',
                street: addressInfo.street || '',
                streetNumber: addressInfo.street_number || '',
                fullAddress: result.result.address || ''
            });
        }
    } catch (error) {
        console.error('获取详细地址失败:', error);
        Object.assign(selectAddress, {
            lat: location.lat,
            lng: location.lng,
            name: location.name,
            province: '',
            city: '',
            district: '',
            street: '',
            streetNumber: '',
            fullAddress: ''
        });
    }

    const position = new TMap.LatLng(location.lat, location.lng);

    if (currentMarker.value) {
        currentMarker.value.setGeometries([{
            id: 'selected',
            position: position,
        }]);
    } else {
        currentMarker.value = new TMap.MultiMarker({
            map: map.value,
            geometries: [{
                id: 'selected',
                position: position,
            }]
        });
    }

    showInfoWindow(position, location.name);
};

const searchByKeyword = async () => {
    if (!searchForm.address.trim()) {
        return;
    }

    try {
        // 使用搜索服务进行搜索
        const result = await search.value.searchRectangle({
            keyword: searchForm.address,
            bounds: map.value.getBounds(),
        });

        addressList.value = result.data;

        // 清除之前的搜索标记
        if (markers.value) {
            // 清除之前的标记
            markers.value.setGeometries([]);

            // 添加新的搜索结果标记
            const geometries = result.data.map((item, index) => ({
                id: String(index),
                position: item.location,
            }));

            markers.value.setGeometries(geometries);
        }
    } catch (error) {
        console.error('搜索失败:', error);
        ElMessage.error('搜索失败，请重试');
    }
};

const selectSearchResult = (item) => {
    updateSelection({
        lat: item.location.lat,
        lng: item.location.lng,
        name: item.title
    });

    map.value.panTo(item.location);
};

const isSelected = (item) => {
    return selectAddress.lat === item.location.lat
        && selectAddress.lng === item.location.lng;
};

const Save = () => {
    if (callback.value) {
        callback.value(selectAddress);
    }
    handleClose();
};

const handleClose = () => {
    if (map.value) {
        map.value.destroy();
        map.value = null;
    }
    visible.value = false;
};

// 导出组件方法供外部使用
defineExpose({
    OpenToast
});

// 生命周期钩子
onBeforeUnmount(() => {
    if (map.value) {
        map.value.destroy();
        map.value = null;
    }
});

// 预加载地图脚本
// 可以在组件挂载后提前加载脚本，提高用户体验
onMounted(() => {
    // 可以选择是否提前加载
    // loadMapScripts().catch(error => console.error('预加载地图脚本失败:', error));
});
</script>

<style lang="scss" scoped>
.location-select-dialog {
    :deep(.el-dialog__body) {
        padding: 0;
    }

    :deep(.el-dialog__header) {
        padding: 15px 20px;
        border-bottom: 1px solid #eee;

        .el-dialog__title {
            font-size: 18px;
            font-weight: 500;
        }
    }

    :deep(.el-dialog__footer) {
        padding: 15px 20px;
        border-top: 1px solid #eee;
    }
}

.address-select {
    height: 600px; // 修改固定高度
    display: flex;
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.search-panel {
    width: 400px;
    border-right: 1px solid #eee;
    display: flex;
    flex-direction: column;
    background: #fff;

    .panel-header {
        padding: 20px;
        border-bottom: 1px solid #eee;

        h2 {
            margin: 0;
            font-size: 24px;
            color: #303133;
        }

        .subtitle {
            margin: 8px 0 0;
            color: #909399;
            font-size: 14px;
        }
    }

    .search-box {
        padding: 20px;
        border-bottom: 1px solid #eee;
    }

    .search-results {
        flex: 1;
        overflow-y: auto;
        padding: 0 10px;

        .result-item {
            padding: 15px;
            cursor: pointer;
            border-radius: 4px;
            margin: 5px 0;
            transition: all 0.3s;

            &:hover {
                background: #f5f7fa;
            }

            &.active {
                background: #ecf5ff;
                border: 1px solid #409EFF;
            }

            .item-content {
                display: flex;
                align-items: flex-start;

                .el-icon {
                    color: #409EFF;
                    font-size: 20px;
                    margin-right: 12px;
                    margin-top: 2px;
                }

                .item-details {
                    flex: 1;

                    .item-name {
                        font-weight: 500;
                        color: #303133;
                        margin-bottom: 4px;
                    }

                    .item-address {
                        font-size: 12px;
                        color: #909399;
                    }
                }
            }
        }
    }

    .selected-address {
        padding: 20px;
        border-top: 1px solid #eee;

        .info-card {
            background: #f5f7fa;
            border-radius: 8px;
            padding: 15px;

            .card-header {
                display: flex;
                align-items: center;
                margin-bottom: 12px;

                .el-icon {
                    color: #67c23a;
                    margin-right: 8px;
                    font-size: 18px;

                    &.success {
                        color: #67c23a;
                    }
                }

                span {
                    font-weight: 500;
                    color: #303133;
                }
            }

            .card-content {
                .address-name {
                    font-size: 14px;
                    color: #606266;
                    margin-bottom: 8px;
                }

                .address-detail {
                    margin: 10px 0;

                    p {
                        margin: 5px 0;
                        color: #606266;
                        font-size: 13px;
                    }

                    .address-components {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 8px;

                        span {
                            color: #909399;
                            font-size: 12px;
                        }
                    }
                }

                .coordinates {
                    font-size: 12px;
                    color: #909399;
                    line-height: 1.6;
                }
            }

            .card-actions {
                margin-top: 15px;
                display: flex;
                gap: 10px;
            }
        }
    }
}

.map-container {
    flex: 1;
    position: relative;

    #containerMap {
        width: 100%;
        height: 100%;
    }
}

// 自定义信息窗体样式
:deep(.custom-info-window) {
    padding: 10px;

    h3 {
        margin: 0 0 5px;
        font-size: 16px;
        color: #303133;
    }

    p {
        margin: 0;
        font-size: 12px;
        color: #909399;
    }
}
</style>
