define(["./binder","./triggerEvent"],function(e,t){return function(r,i){this.handle=r.handle,e(this,r),this.on(function(e,n){t(e,n,r,i.getLatency())}),this.destroy=this.off}});