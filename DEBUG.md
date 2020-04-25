## Debug

1、多级请求路径时，静态资源会添加额外的路径，导致静态资源404

==注意：==asserts前面加“/”————找了一下午bug

```html
<script type="text/javascript" th:src="@{asserts/js/bootstrap.min.js}"></script>
```

```html
<script type="text/javascript" th:src="@{/asserts/js/bootstrap.min.js}"></script>
```

## Problem

