(function () {
   const d = {}
   const isMovable = targ => {
      return targ.classList?.contains("vss-movable")
   }
   document.addEventListener("mousedown", e => {
      const closestDialog = e.target.closest(".v-overlay__content")
      //const title = closestDialog?.querySelector(".v-card-title")
      const title = closestDialog?.querySelector(".v-toolbar-title__placeholder")
      if (e.button === 0 && closestDialog != null && isMovable(e.target) || isMovable(e.target.parentNode)) {
         d.el = closestDialog // movable element
         d.handle = title // enable dlg to be moved down beyond bottom
         d.mouseStartX = e.clientX
         d.mouseStartY = e.clientY
         d.elStartX = d.el.getBoundingClientRect().left
         d.elStartY = d.el.getBoundingClientRect().top
         d.el.style.position = "fixed"
         d.el.style.margin = 0
         d.oldTransition = d.el.style.transition
         d.el.style.transition = "none"
      }
   })
   document.addEventListener("mousemove", e => {
      if (d.el === undefined) return
      d.el.style.left = Math.min(
         Math.max(d.elStartX + e.clientX - d.mouseStartX, 0),
         window.innerWidth - d.el.getBoundingClientRect().width) + "px"
      d.el.style.top = Math.min(Math.max(d.elStartY + e.clientY - d.mouseStartY, 0),
         window.innerHeight - d.handle.getBoundingClientRect().height) + "px"
   })
   document.addEventListener("mouseup", () => {
      if (d.el === undefined) return
      d.el.style.transition = d.oldTransition
      d.el = undefined
   })
})()
// Global Const for JWT
app.config.globalProperties.$jwt = ''
// Spanish collator
app.config.globalProperties.$collator = new Intl.Collator('es')
// Fetch Text or JSON objects
app.config.globalProperties.$fetch = async (input, init) => {
   const response = await fetch(input, init)
   if (!response.ok) throw Error(await response.text())
   const contentType = response.headers.get("content-type")
   return contentType.includes("text/plain")
      ? await response.text()
      : await response.json()
}
// Fetch and image as blob
app.config.globalProperties.$fetchImage = async (input, init) => {
   const response = await fetch(input, init)
   if (!response.ok) throw Error(await response.text())
   return URL.createObjectURL(await response.blob())
}
//
app.config.globalProperties.$toast = Swal.mixin({
   toast: true,
   position: 'top-end',
   showConfirmButton: false,
   timer: 3000,
   timerProgressBar: true,
   didOpen: (evtToast) => {
      evtToast.addEventListener('mouseenter', Swal.stopTimer)
      evtToast.addEventListener('mouseleave', Swal.resumeTimer)
   }
})
// app.config.globalProperties.$toast2300 = Swal.mixin({
//     toast: true,
//     position: 'top-end',
//     showConfirmButton: false,
//     timer: 2300,
//     timerProgressBar: true,
//     didOpen: (evtToast) => {
//         evtToast.addEventListener('mouseenter', Swal.stopTimer)
//         evtToast.addEventListener('mouseleave', Swal.resumeTimer)
//     }
// })

//
app.config.globalProperties.$showError = (title, text) => {
   Swal.fire({ icon: 'error', title: title, text: text })
}
//
app.config.globalProperties.$swalConfirm = async (title, text, icon) => {
   return await Swal.fire({
      title: title,
      text: text,
      icon: icon,
      showCancelButton: true,
      //html: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Confirmar'
   })
}
//
// Export Excel/CSV
app.config.globalProperties.$dataToCSV = data => {
   const csvRows = []
   const headers = Object.keys(data[0])
   csvRows.push(headers.join(','))
   for (const row of data) {
      const values = headers.map(header => {
         const escaped = ('' + row[header]).normalize('NFD').replace(/[\u0300-\u036f]/g, "")
         return `"${escaped}"`
      })
      csvRows.push(values)
   }
   return csvRows.join('\n')
}
//
// Generate a data blob object & download link
app.config.globalProperties.$downloadCSV = (data, file) => {
   const blob = new Blob([data], { type: 'text/csv' })
   const url = window.URL.createObjectURL(blob)
   const a = document.createElement('a')
   //a.setAttribute('hidden', '')
   a.setAttribute('href', url)
   a.setAttribute('download', file)
   document.body.appendChild(a)
   a.click()
   document.body.removeChild(a)
}
//
//
app.config.globalProperties.$ddMMyyFormat = (strDate) => {
   const arr = strDate.split('-')
   return `${arr[2]}/${arr[1]}/${arr[0]}`
}
//
//
app.config.globalProperties.$fetchDownloadPdf = async (input, init) => {
   const response = await fetch(input, init)
   if (!response.ok) throw Error(await response.text())
   const url = URL.createObjectURL(await response.blob())
   const a = document.createElement('a')
   a.setAttribute('download', 'Reporte-sistema-dat.pdf')
   a.setAttribute('href', url)
   //a.setAttribute('target', '_blank')
   a.click()
   //document.body.removeChild(a)
   URL.revokeObjectURL(url)
}