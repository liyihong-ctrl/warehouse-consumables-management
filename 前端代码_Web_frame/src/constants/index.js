export const NO_PERIEW = 0

export const UNDER_REVIEW = 1

export const NOT_PASS = 2

export const EXAMINATION_PASSED = 3

export const COMPLETED = 4

export const APPROVAL_STATUS = {
    [NO_PERIEW]: '未审核',
    [UNDER_REVIEW]: '审核中',
    [EXAMINATION_PASSED]: '审核通过',
    [NOT_PASS]: '未通过',
    [COMPLETED]: '已完成'
}

//#region StaticType

export const CONSUMABLE_EXPORT = 1          // 消耗品出库

export const CONSUMABLE_REDUCED_WORK = 2    // 消耗品在工减少

export const CONSUMABLE_IMPORT = 3          // 消耗品入库

export const COMPREHENSIVE_QUANTITY = 4     // 综合数量

//#endregion
